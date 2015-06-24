import {Redirect} from 'aurelia-router';
import {AppModel} from 'app-model';

export class App {

    configureRouter(config, router) {
        config.title = 'Insane Auctions';
        config.addPipelineStep('authorize', RegisteredStep)
        config.map([
            {route: ['','connect'], moduleId: 'loading', nav: true, title: 'Loading'},
            {route: ['register'], moduleId: 'register', nav: true, title: 'Register'},
            {route: ['auction'], moduleId: 'auction', nav: true, auth: true, title: 'Auction'}
        ]);
        this.router = router;
    }
}

class RegisteredStep {

    static inject() {
        return [AppModel];
    }

    constructor(model) {
        this.model = model;
    }

    run(routingContext, next) {
        // Check if the route has an "auth" key
        // The reason for using `nextInstructions` is because
        // this includes child routes.
        if (routingContext.nextInstructions.some(i => i.config.auth)) {
            if (!this.model.loaded()) {
                console.log('redirect -> connect');
                return next.cancel(new Redirect('connect'));
            }
        }
        return next();
    }
}