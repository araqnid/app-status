import React from 'react';
import {loadStatus} from "./statusObservable";
import {StatusDetails} from "./StatusDetails";
import {RefreshState} from "./RefreshState";
import {autoRefresh} from "./refresh";

const EMPTY_STATUS_PAGE = {version: null, readiness: null, status: null};

function updateStatusPageMember(member, value) {
    return state => {
        const statusPage = state.values || EMPTY_STATUS_PAGE;
        return {...state, values: {...statusPage, [member]: value}};
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

export default class Status extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            refresh: {paused: true, interval: 500},
            values: EMPTY_STATUS_PAGE,
            loadingError: null
        };
        this._subscription = null;
    }

    render() {
        if (this.state.loadingError !== null) {
            return <div key="error">Failed to load status: {this.state.loadingError}</div>;
        }

        if (!this.state.values.version || !this.state.values.readiness || !this.state.values.status) {
            return <div key="absent"/>;
        }

        return (
            <div key="present">
                <StatusDetails {...this.state.values} />
                <RefreshState {...this.state.refresh} controls={this._controls}/>
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
        this.setState(updateRefreshPaused(true));
    }

    unpause() {
        this._unsubscribe();
        this.setState(updateRefreshPaused(false), () => this._subscribe());
    }

    kick() {
        this._unsubscribe();
        this._subscribe();
    }

    updateRefreshInterval(newInterval) {
        this._unsubscribe();
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
        this._subscription = this._observable.subscribe(
            ({type, payload, error = false}) => {
                if (error) {
                    this.setState({values: EMPTY_STATUS_PAGE, loadingError: payload});
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
                    }
                }
            }
        );
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
        return autoRefresh(interval)(loadStatus);
    }
}
