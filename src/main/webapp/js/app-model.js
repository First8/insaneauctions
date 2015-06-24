import {singleton} from 'aurelia-framework';

@singleton()
export class AppModel {

    constructor() {
        this.nickname = null;
        this.auction = null;
    }

    reset() {
        this.nickname = null;
        this.auction = null;
    }

    loggedId() {
        return this.nickname != null;
    }

    loaded() {
        return this.nickname != null && this.auction != null;
    }

}