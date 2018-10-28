import Observable from "zen-observable";
import * as ajax from "./ajax";
import {merge} from "zen-observable/extras";
import {autoRefresh} from "./refresh";

const accept = mimeType => ({ headers: { "Accept": mimeType } });
const statusAjax = ajax.get("/_api/info/status", accept("application/json"));
const versionAjax = ajax.get("/_api/info/version", accept("application/json"));
const readinessAjax = ajax.get("/_api/info/readiness", accept("text/plain"));

function asActions(observable, type) {
    return new Observable(observer => {
        observable.subscribe(
            value => { observer.next({ type: type, payload: value }) },
            error => { observer.next({ type: `${type}.error`, payload: error, error: true }); observer.complete() },
            () => { observer.complete() }
        );
    });
}

const statusAsActions = asActions(statusAjax, "status");
const versionAsActions = asActions(versionAjax, "version");
const readinessAsActions = asActions(readinessAjax, "readiness");

const loadStatus = merge(statusAsActions, versionAsActions, readinessAsActions);

class StatusSubscription {
    constructor(observer) {
        this._observer = observer;
        this._subscription = null;
        this._refreshState = { paused: true, interval: 500 };

        this._begin();
    }

    pause() {
        this._refreshState.paused = true;
        this._emitState();
        this.unsubscribe();
    }

    unpause() {
        this._refreshState.paused = false;
        this._emitState();
        this._subscribe();
    }

    kick() {
        this.unsubscribe();
        this._subscribe();
    }

    _begin() {
        this._observer.next({ type: '_controls', payload: this }); // hax
        this._emitState();
        this._subscribe();
    }

    _emitState() {
        this._observer.next({ type: "refreshState", payload: this._refreshState });
    }

    _subscribe() {
        this._subscription = this._underlyingObservable.subscribe(
            value => this._observer.next(value)
        );
    }

    get _underlyingObservable() {
        if (this._refreshState.paused)
            return loadStatus;
        return autoRefresh(this._refreshState.interval)(loadStatus);
    }

    unsubscribe() {
        if (this._subscription) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }
    }
}

const statusObservable = new Observable(observer => new StatusSubscription(observer));

export default statusObservable;
