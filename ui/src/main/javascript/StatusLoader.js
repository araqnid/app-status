import Observable from "zen-observable";
import * as ajax from "./ajax";
import {asActions, concat} from "./observables";
import {merge} from "zen-observable/lib/extras";
import {autoRefresh} from "./refresh";

const accept = mimeType => ({headers: {"Accept": mimeType}});
const statusAjax = ajax.get("/_api/info/status", accept("application/json"));
const versionAjax = ajax.get("/_api/info/version", accept("application/json"));
const readinessAjax = ajax.get("/_api/info/readiness", accept("text/plain"));

const statusAsActions = asActions("status")(statusAjax);
const versionAsActions = asActions("version")(versionAjax);
const readinessAsActions = asActions("readiness")(readinessAjax);

const loadStatus = concat(
    Observable.of({type: "refresh-start"}),
    merge(statusAsActions, versionAsActions, readinessAsActions),
    Observable.of({type: "refresh-complete"})
);

function applyAutoRefresh(refreshInterval) {
    return next => {
        if (refreshInterval)
            return autoRefresh(refreshInterval)(next);
        else
            return next;
    };
}

class StatusLoader {
    constructor(refreshInterval) {
        this.subscribers = new Set();
        this._subscription = null;
    }

    stop() {
        this._unsubscribe();
    }

    start(refreshInterval) {
        this._subscribe(refreshInterval);
    }

    _emit(type, payload, error) {
        const action = {type, payload, error};
        for (const subscriber of this.subscribers) {
            subscriber.next(action);
        }
    }

    _unsubscribe() {
        if (this._subscription) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }
    }

    _subscribe(refreshInterval) {
        this._unsubscribe();
        this._subscription = applyAutoRefresh(refreshInterval)(loadStatus).subscribe(
            ({type, payload, error = false}) => {
                this._emit(type, payload, error);
            }
        );
    }

    [Symbol.observable]() {
        return new Observable(observer => {
            this.subscribers.add(observer);
            return () => {
                this.subscribers.delete(observer);
                if (this.subscribers.size === 0) {
                    this._unsubscribe();
                }
            };
        });
    }
}

export default StatusLoader;
