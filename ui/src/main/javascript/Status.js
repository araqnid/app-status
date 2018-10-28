import React from 'react';
import {StatusDetails} from "./StatusDetails";
import {RefreshState} from "./RefreshState";

const Status = ({loadingError, values, refresh, controls}) => {
    if (loadingError !== null) {
        return <div key="error">
            Failed to load status: {loadingError}
        </div>;
    }

    if (!values.version || !values.readiness || !values.status) {
        return <div key="absent">
        </div>;
    }

    return (
        <div key="present">
            <StatusDetails {...values} />
            <RefreshState {...refresh} controls={controls} />
        </div>
    );
};

export default Status;
