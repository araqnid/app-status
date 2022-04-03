import React from 'react'
import StatusDetails from "./StatusDetails"
import RefreshState from "./RefreshState"

const Status = ({loadingError, values: { version, status, readiness}, refresh: { paused, interval }, controls}) => {
    if (loadingError !== null) {
        return (
            <div key="error">
                Failed to load status: {loadingError.toString()}
            </div>
        )
    }

    if (!version || !readiness || !status) {
        return (
            <div key="absent"/>
        )
    }

    return (
        <div key="present">
            <StatusDetails version={version} status={status} readiness={readiness} />
            <RefreshState paused={paused} interval={interval} controls={controls} />
        </div>
    );
};

export default Status
