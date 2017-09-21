import Bus from './Bus'

export default class MemoBus {
    constructor(name) {
        this.memory = {};
        this.bus = new Bus(name);
    }
    dispatch(type, payload) {
        this.memory[type] = payload;
        this.bus.broadcast(type, payload);
    }
    subscribeAll(handlers, owner) {
        for (const type of Object.keys(handlers)) {
            this.subscribe(type, handlers[type], owner);
        }
    }
    subscribe(type, target, owner) {
        this.bus.subscribe(type, target, owner);
        if (this.memory[type]) {
            target(this.memory[type]);
        }
    }
    unsubscribe(owner) {
        this.bus.unsubscribe(owner);
    }
    isEmpty() {
        return this.bus.isEmpty();
    }
}
