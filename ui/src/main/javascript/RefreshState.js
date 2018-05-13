import React from 'react';

export const RefreshState = ({ paused, interval, store_kick, store_pause, store_unpause}) => {
    if (paused) {
        return <div id="refresh-state" className="paused"><button onClick={store_kick}>Refresh</button><button onClick={store_unpause}>Start auto-refresh</button></div>;
    }
    else {
        return <div id="refresh-state" className="unpaused">Auto-refresh interval: {interval} <button onClick={store_pause}>Stop auto-refresh</button></div>;
    }
};
