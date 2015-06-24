import {EventAggregator} from 'aurelia-event-aggregator';
import {inject} from 'aurelia-framework';
import {Router} from 'aurelia-router';
import {AppModel} from 'app-model';
import {WebAPI} from 'webapi';

@inject(Router, AppModel, WebAPI, EventAggregator)
export class Register {

    constructor(router, appModel, webapi, ea) {
        this.nickname = '';
        this.router = router;
        this.appModel = appModel;
        this.webapi = webapi;
        this.ea = ea;
        this.dispose = null;
    }

    activate() {
        this.dispose = this.ea.subscribe('events', dto => {
            console.log('event', dto);
            this.handleRegister(dto);
        });
    }

    deactivate() {
        this.dispose();
    }

    register() {
        this.webapi.register(this.nickname);
    }

    handleRegister(dto) {
        if( dto.event != 'register') {
            return;
        }

        if (dto.dateLoggedIn != null) {
            this.appModel.nickname = this.nickname;
            this.router.navigate("auction");
        }
    }
}