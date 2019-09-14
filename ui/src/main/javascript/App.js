import React from 'react';
import Observable from 'zen-observable';
import {merge} from "zen-observable/extras";
import {autoRefresh} from "./refresh";
import {LoadingIndicator} from "./LoadingIndicator";
import {asActions, concat} from "./observables";
import Status from "./Status";
import * as ajax from "./ajax";

const EMPTY_STATUS_PAGE = {version: null, readiness: null, status: null};

function updateStatusPageMember(member, value) {
    return state => {
        const statusPage = state.values || EMPTY_STATUS_PAGE;
        return {...state, loadingError: null, values: {...statusPage, [member]: value}};
    }
}

function updateRefreshPaused(paused) {
    return state => {
        const refresh = state.refresh;
        return {...state, refresh: {...refresh, paused}};
    }
}

function updateRefreshInterval(interval) {
    return state => {
        const refresh = state.refresh;
        return {...state, refresh: {...refresh, interval}};
    }
}

function markLoading(loading) {
    return state => {
        return {...state, loading };
    }
}

function setLoadingError(loadingError) {
    return state => {
        return {...state, loadingError, values: EMPTY_STATUS_PAGE };
    }
}

const accept = mimeType => ({ headers: { "Accept": mimeType } });
const statusAjax = ajax.get("/_api/info/status", accept("application/json"));
const versionAjax = ajax.get("/_api/info/version", accept("application/json"));
const readinessAjax = ajax.get("/_api/info/readiness", accept("text/plain"));

const statusAsActions = asActions("status")(statusAjax);
const versionAsActions = asActions("version")(versionAjax);
const readinessAsActions = asActions("readiness")(readinessAjax);

const loadStatus = merge(statusAsActions, versionAsActions, readinessAsActions);

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            refresh: {paused: true, interval: 500},
            values: EMPTY_STATUS_PAGE,
            loadingError: null,
            loading: false
        };
        this._subscription = null;
    }

    render() {
        const { loadingError, values, refresh, loading } = this.state;

        return (
            <div>
                <LoadingIndicator loading={loading ? "true" : undefined} />
                <Status loadingError={loadingError} values={values} refresh={refresh} controls={this._controls} />
            </div>
        );
    }

    componentDidMount() {
        this._subscribe()
    }

    componentWillUnmount() {
        this._unsubscribe();
    }

    pause() {
        this._unsubscribe();
        this.setState(markLoading(false));
        this.setState(updateRefreshPaused(true));
    }

    unpause() {
        this._unsubscribe();
        this.setState(markLoading(false));
        this.setState(updateRefreshPaused(false), () => this._subscribe());
    }

    kick() {
        this._unsubscribe();
        this.setState(markLoading(false), () => this._subscribe());
    }

    updateRefreshInterval(newInterval) {
        this._unsubscribe();
        this.setState(markLoading(false));
        this.setState(updateRefreshInterval(newInterval), () => this._subscribe());
    }

    get _controls() {
        return {
            pause: this.pause.bind(this),
            unpause: this.unpause.bind(this),
            kick: this.kick.bind(this),
            updateRefreshInterval: this.updateRefreshInterval.bind(this)
        }
    }

    _subscribe() {
        if (this._subscription) throw new Error("Subscription already present");
        this.setState(markLoading(true), () => {
            this._subscription = this._observable.subscribe(
                ({type, payload, error = false}) => {
                    if (error) {
                        this.setState(setLoadingError(payload));
                        this.setState(markLoading(false));
                    }
                    else {
                        switch (type) {
                            case "status":
                                this.setState(updateStatusPageMember('status', payload));
                                break;
                            case "version":
                                this.setState(updateStatusPageMember('version', payload));
                                break;
                            case "readiness":
                                this.setState(updateStatusPageMember('readiness', payload));
                                break;
                            case "refresh-start":
                                this.setState(markLoading(true));
                                break;
                            case "refresh-complete":
                                this.setState(markLoading(false));
                                break;
                        }
                    }
                },
                error => {
                    console.error("subscription terminated unexpectedly", error);
                    this.setState(markLoading(false));
                    this._subscription = null;
                },
                () => {
                    this.setState(markLoading(false));
                    this._subscription = null;
                }
            );
        });
    }

    _unsubscribe() {
        if (this._subscription) {
            this._subscription.unsubscribe();
            this._subscription = null;
        }
    }

    get _observable() {
        const { paused, interval } = this.state.refresh;
        if (paused)
            return loadStatus;
        return autoRefresh(interval)(
            concat(
                Observable.of({ type: "refresh-start", payload: null }),
                loadStatus,
                Observable.of({ type: "refresh-complete", payload: null })
            )
        );
    }
}
