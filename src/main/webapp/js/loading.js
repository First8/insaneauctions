import {EventAggregator} from 'aurelia-event-aggregator';
import {singleton} from 'aurelia-framework';
import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {AppModel} from 'app-model';
import {WebAPI} from 'webapi';

@singleton()
@inject(Router,AppModel,WebAPI,EventAggregator)
export class Loading {

    constructor(router,appModel,webapi,ea) {
        this.router = router;
        this.model = appModel;
        this.webapi = webapi;
        this.ea = ea;
        this.dispose = null;
    }

    activate() {
        this.dispose = this.ea.subscribe('events', dto => {
            console.log('event', dto);
            this.handleRegistered(dto);
            this.handleCurrentAuction(dto);

        });
        this.webapi.registered();

    }

    deactivate() {
        this.dispose();
    }

    handleRegistered(dto) {
        if( dto.event != 'registered') {
            return;
        }

        if(dto.loggedIn && dto.nickname != null ) {
            this.webapi.initWS();
            this.model.nickname = dto.nickname;
            this.webapi.currentAuction();
        } else {
            this.model.reset();
            this.router.navigate("register");
        }
    }

    handleCurrentAuction(dto) {
        if( dto.event != 'auction') {
            return;
        }
        this.model.auction = dto;
        this.router.navigate("auction");
    }
}