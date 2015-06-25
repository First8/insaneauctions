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

    loaded() {
        return this.nickname != null && this.auction != null;
    }

}