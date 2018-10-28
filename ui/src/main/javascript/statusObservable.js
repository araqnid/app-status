import Observable from "zen-observable";
import * as ajax from "./ajax";
import {merge} from "zen-observable/extras";

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
        this._cancelTimer();
    }

    unpause() {
        this._refreshState.paused = false;
        this._emitState();
        if (this._refreshTimer == null && this._subscription == null)
            this._startLoading();
    }

    kick() {
        if (!this._subscription) {
            this._cancelTimer();
            this._startLoading();
        }
    }

    updateRefreshInterval(newInterval) {
        this._refreshState.interval = newInterval;
        this._emitState();
        if (this._refreshTimer != null) {
            this._cancelTimer();
            this._scheduleRefresh();
        }
    }

    _begin() {
        this._observer.next({ type: '_controls', payload: this }); // hax
        this._emitState();
        this._startLoading();
    }

    _emitState() {
        this._observer.next({ type: "refreshState", payload: this._refreshState });
    }

    _startLoading() {
        loadStatus.subscribe(
            value => { this._observer.next(value); },
            error => { /* unexpected */ },
            () => {
                this._subscription = null;
                if (!this._refreshState.paused) {
                    this._scheduleRefresh();
                }
            }
        );
    }

    _timerTick() {
        this._refreshTimer = null;
        this._startLoading();
    }

    unsubscribe() {
        if (this._subscription) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }
        this._cancelTimer();
    }

    _scheduleRefresh() {
        this._refreshTimer = window.setTimeout(() => this._timerTick(), this._refreshState.interval);
    }

    _cancelTimer() {
        if (this._refreshTimer) {
            window.clearTimeout(this._refreshTimer);
            this._refreshTimer = null;
        }
    }
}

const statusObservable = new Observable(observer => new StatusSubscription(observer));

export default statusObservable;
