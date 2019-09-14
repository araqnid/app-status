import React from 'react';

const Button = ({onClick, type = "secondary", children}) => {
    return (
        <button type="button" className={`btn btn-block btn-${type}`} onClick={onClick}>{children}</button>
    )
};

export const RefreshState = ({paused, interval, controls}) => {
    const inner = paused ? (
            <>
                <Button type="secondary" onClick={controls.kick}>Refresh</Button>
                <Button type="primary" onClick={controls.unpause}>Start auto-refresh</Button>
            </>
        )
        : (
            <>
                <p className="card-text">Auto-refresh interval: {interval}</p>
                <Button type="primary" onClick={controls.pause}>Stop auto-refresh</Button>
            </>
        );

    return (
        <div className="card float-right" style={{ width: "18rem", margin: 16 }}>
            <div className="card-body">
                <h5 className="card-title">Refresh</h5>
                {inner}
            </div>
        </div>
    );
};
