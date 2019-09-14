import React, {useEffect, useMemo, useRef, useState} from 'react';
import Observable from "zen-observable";
import {LoadingIndicator} from "./LoadingIndicator";
import Status from "./Status";
import StatusLoader from "./StatusLoader";

const App = ({}) => {
    const [paused, setPaused] = useState(false);
    const [refreshInterval, setRefreshInterval] = useState(500);
    const [status, setStatus] = useState(null);
    const [version, setVersion] = useState(null);
    const [readiness, setReadiness] = useState(null);
    const [loadingError, setLoadingError] = useState(null);
    const [loading, setLoading] = useState(false);
    const statusLoader = useRef(new StatusLoader(refreshInterval));
    useEffect(() => {
        const subscription = Observable.from(statusLoader.current).subscribe(
            ({type, payload, error = false}) => {
                if (error) {
                    setLoadingError(payload);
                    setLoading(false);
                } else {
                    switch (type) {
                        case "status":
                            setStatus(payload);
                            break;
                        case "version":
                            setVersion(payload);
                            break;
                        case "readiness":
                            setReadiness(payload);
                            break;
                        case "refresh-start":
                            setLoading(true);
                            break;
                        case "refresh-complete":
                            setLoading(false);
                            break;
                    }
                }
            });
        statusLoader.current.start(paused ? null : refreshInterval);
        return () => {
            subscription.unsubscribe();
        };
    }, []);
    const controls = useMemo(() => {
        return {
            pause() {
                statusLoader.current.stop();
                setLoading(false);
                setPaused(true);
            },

            unpause() {
                statusLoader.current.stop();
                setLoading(false);
                setPaused(false);
                statusLoader.current.start(refreshInterval);
            },

            kick() {
                statusLoader.current.stop();
                setLoading(false);
                statusLoader.current.start();
            },

            updateRefreshInterval(newInterval) {
                setRefreshInterval(newInterval);
                if (!paused) {
                    statusLoader.current.stop();
                    setLoading(false);
                    statusLoader.current.start(newInterval);
                }
            }
        };
    }, []);

    return (
        <div>
            <LoadingIndicator loading={loading ? "true" : undefined}/>
            <Status loadingError={loadingError}
                    values={{status, version, readiness}}
                    refresh={{paused, interval: refreshInterval}}
                    controls={controls}/>
        </div>
    );
};

export default App;
