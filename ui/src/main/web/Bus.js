import _ from 'lodash';

export default class Bus {
    constructor(name) {
        this.name = name;
        this.subscribers = { dead: [] }
    }
    broadcast(type, data) {
        let typeSubscribers = this.subscribers[type];
        if (typeSubscribers === undefined || typeSubscribers.length === 0) {
            typeSubscribers = this.subscribers.dead;
            if (console && console.log) console.log((this.name ? this.name : "BUS"), type + " (dead)", data);
        } else {
            if (console && console.log) console.log((this.name ? this.name : "BUS"), type, data);
        }
        typeSubscribers.forEach(subscriber => {
            const receiver = subscriber[0];
            const actor = subscriber[1];
            receiver.call(actor, data);
        });
    }
    subscribe(type, receiver, actor) {
        if (this.subscribers[type] === undefined) {
            this.subscribers[type] = [];
        }
        this.subscribers[type].push([receiver, actor]);
    }
    unsubscribe(actor) {
        for (const type of Object.keys(this.subscribers)) {
            _.remove(this.subscribers[type], subscriber => subscriber[1] === actor);
        }
    }
    isEmpty() {
        const counts = [];
        for (const type of Object.keys(this.subscribers)) {
            counts.push(this.subscribers[type].length);
        }
        return _.sum(counts) === 0;
    }
}
