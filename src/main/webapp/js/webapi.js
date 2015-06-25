import 'bootstrap';

import {EventAggregator} from 'aurelia-event-aggregator';
import {singleton} from 'aurelia-framework';
import {inject} from 'aurelia-framework';
import {HttpClient} from 'aurelia-http-client';

@singleton()
@inject(HttpClient, EventAggregator)
export class WebAPI {

    constructor(http, ea) {
        this.timer = null;
        this.ea = ea;
        this.http = http;
        this.http.configure(x => {
            x.withHeader("Content-Type", "application/x-www-form-urlencoded");
        });
        this.socket = null;
    }

    initWS() {
    }
    
    registered() {
        this.http.get('api/register')
            .then(response => JSON.parse(response.response))
            .then(dto => this.publishEvent(dto));
    }

    register(nickname) {
        this.http.post('api/register', jQuery.param({'nickname': nickname}))
            .then(response => JSON.parse(response.response))
            .then(dto => this.publishEvent(dto));
    }

    currentAuction() {
        this.http.get('api/auction')
            .then(response => JSON.parse(response.response))
            .then(dto => this.publishEvent(dto));
    }

    watchAuction(interval) {
        clearInterval(this.timer);
        this.timer = setInterval(() => {
            this.http.get('api/auction')
                .then(response => {
                        if (response.statusCode != 200) {
                            return {event: 'http-error', message: 'No HTTP connection'};
                        } else {
                            return JSON.parse(response.response);
                        }
                    }, error => {
                        return {event: 'http-error', message: 'No HTTP connection'};
                    })
                .then(dto => this.publishEvent(dto), error => {
                })
        }, interval);
    }

    bid(value) {
        return this.http.post('api/auction', jQuery.param({'value': value}))
            .then(response => JSON.parse(response.response))
            .then(dto => this.publishEvent(dto));
    }

    publishEvent(dto) {
        if (dto != null) {
            this.ea.publish('events', dto);
        }
    }
}