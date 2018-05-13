import React from 'react';
import statusObservable from "./statusObservable";
import {LoadingStatusDetails} from "./StatusDetails";
import {RefreshState} from "./RefreshState";

export default class Status extends React.Component {
    constructor(props) {
        super(props);
        this.state = { statusPage: null, refreshState: null, readiness: null, version: null, loadingError: null };
        this._subscription = null;
        this._controls = null;
    }

    render() {
        if (!this.state.refreshState) {
            return <div key="absent"/>;
        }

        if (this.state.loadingError !== null) {
            return <div key="error">Failed to load status: { this.state.loadingError }</div>;
        }

        return (
            <div key="present">
                <LoadingStatusDetails
                    version={this.state.version} statusPage={this.state.statusPage} readiness={this.state.readiness}
                />
                <RefreshState {...this.state.refreshState}
                              store_pause={() => this._controls.pause()}
                              store_unpause={() => this._controls.unpause()}
                              store_kick={() => this._controls.kick()}
                />
            </div>
        );
    }

    componentDidMount() {
        this._subscription = statusObservable.subscribe(
            ({ type, payload, error = false }) => {
                if (error) {
                    this.setState({ status: null, version: null, readiness: null, loadingError: payload });
                }
                else {
                    switch (type) {
                        case "_controls":
                            this._controls = payload;
                            break;
                        case "refreshState":
                            this.setState({ refreshState: payload });
                            break;
                        case "status":
                            this.setState({ statusPage : payload, loadingError: null });
                            break;
                        case "version":
                            this.setState({ version : payload, loadingError: null });
                            break;
                        case "readiness":
                            this.setState({ readiness : payload, loadingError: null });
                            break;
                    }
                }
            },
            error => {
                console.error("status observable terminated with error", error);
            },
            () => {
                console.error("status observable terminated")
            }
        );
    }

    componentWillUnmount() {
        if (this._subscription)
            this._subscription.unsubscribe();
    }
}
