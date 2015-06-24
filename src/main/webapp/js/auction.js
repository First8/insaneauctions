import {EventAggregator} from 'aurelia-event-aggregator';
import {inject} from 'aurelia-framework';
import {singleton} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {AppModel} from 'app-model';
import {WebAPI} from 'webapi';

@singleton()
@inject(Router, AppModel, WebAPI, EventAggregator)
export class Auction {

    constructor(router, appModel, webapi, ea) {
        this.router = router;
        this.webapi = webapi;
        this.model = appModel;
        this.ea = ea;
        this.bidValue = null;
        this.clock = '';
        this.countdown = new Countdown();
        this.countdown.start( () => { 
            this.closed = this.countdown.isFinished();
            this.teaser = this.closed ? 'You can\'t have it!' : 'I want it!';
        	this.clock = this.countdown.time();
        	});
        this.closed = false;
        this.teaser = 'I want it!';
        this.dispose = null;
    }

    activate() {
    	console.log("active auction view");
        this.dispose = this.ea.subscribe('events', dto => {
            console.log('event', dto);
            this.handleError(dto);
            this.handleAuction(dto);
            this.handleBid(dto);
        });
        this.countdown.setTargetRemaining(this.model.auction.timeRemaining);
    }

    deactivate() {
        this.dispose();
    }

    handleError(dto) {
        if( dto.event != 'http-error') {
            this.model.error = null;
            return;
        }
        console.log(dto);
        console.log('We have an error message:' + dto.message);
        this.model.error = dto.message;
    }

    handleAuction(dto) {
        if( dto.event != 'auction') {
            return;
        }
        this.model.auction = dto;
        this.countdown.setTargetRemaining(this.model.auction.timeRemaining);
    }

    bid() {
        if (this.bidValue > 0) {
            this.webapi.bid(this.bidValue);
        } else {
            alert("Bid not accepted!");
        }
    }

    handleBid(dto) {
        if( dto.event != 'bid-result') {
            return;
        }
        if (!dto.success) {
            if( dto.reason == 'BID_TOO_LOW') {
                alert("Bid not accepted, too low.");
            } else if( dto.reason == 'NOT_LOGGED_IN') {
                this.router.navigate("connect");
            }
        }
    }
}

class Countdown {

    constructor() {
        this.timer = null;
        this.targetEnd = 0;
    }
    
    isFinished() {
    	return this.targetEnd < new Date().getTime();
    }
    
    setTargetRemaining(r) {
    	this.targetEnd = new Date().getTime() + r;
    }

    start(fn) {
        clearInterval(this.timer);
        this.timer = setInterval( () => { fn(); }, 100);
    }

    stop() {
        clearInterval(this.timer);
    }

    time() {
    	var remaining = this.targetEnd - new Date().getTime();
    	if (remaining<0) remaining = 0;
        var seconds = parseInt((remaining / 1000) % 60)
            , minutes = parseInt((remaining / (1000 * 60)) % 60)
            , hours = parseInt((remaining / (1000 * 60 * 60)) % 24);
        hours = (hours < 10) ? "0" + hours : hours;
        minutes = (minutes < 10) ? "0" + minutes : minutes;
        seconds = (seconds < 10) ? "0" + seconds : seconds;
        return hours + ":" + minutes + ":" + seconds;
    }
}