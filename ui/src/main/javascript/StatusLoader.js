import Observable from "zen-observable";
import * as ajax from "./ajax";
import {asActions, concat} from "./observables";
import {merge} from "zen-observable/lib/extras";
import {autoRefresh} from "./refresh";

function accept(mimeType) {
    return {headers: {"Accept": mimeType}};
}

const loadStatus = concat(
    Observable.of({type: "refresh-start"}),
    merge(
        asActions("status")(ajax.get("/_api/info/status", accept("application/json"))),
        asActions("version")(ajax.get("/_api/info/version", accept("application/json"))),
        asActions("readiness")(ajax.get("/_api/info/readiness", accept("text/plain")))
    ),
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
    constructor() {
        this._subscribers = new Set();
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
        for (const subscriber of this._subscribers) {
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
            this._subscribers.add(observer);
            return () => {
                this._subscribers.delete(observer);
                if (this._subscribers.size === 0) {
                    this._unsubscribe();
                }
            };
        });
    }
}

export default StatusLoader;
