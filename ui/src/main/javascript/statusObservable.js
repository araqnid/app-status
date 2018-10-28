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

export const loadStatus = merge(statusAsActions, versionAsActions, readinessAsActions);
