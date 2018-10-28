import Observable from "zen-observable";
import * as ajax from "./ajax";
import {merge} from "zen-observable/extras";

const accept = mimeType => ({ headers: { "Accept": mimeType } });
const statusAjax = ajax.get("/_api/info/status", accept("application/json"));
const versionAjax = ajax.get("/_api/info/version", accept("application/json"));
const readinessAjax = ajax.get("/_api/info/readiness", accept("text/plain"));

export function asActions(type) {
    return underlying => {
        return new Observable(observer => {
            underlying.subscribe(
                value => { observer.next({ type: type, payload: value }) },
                error => { observer.next({ type: `${type}.error`, payload: error, error: true }); observer.complete() },
                () => { observer.complete() }
            );
        });
    };
}

const statusAsActions = asActions("status")(statusAjax);
const versionAsActions = asActions("version")(versionAjax);
const readinessAsActions = asActions("readiness")(readinessAjax);

export const loadStatus = merge(statusAsActions, versionAsActions, readinessAsActions);
