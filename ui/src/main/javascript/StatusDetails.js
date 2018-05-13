import React from 'react';

export const StatusComponent = props => {
    return <li className={ "status-component priority-" + props.priority.toLowerCase() }>
        <span className="label">{props.priority === "INFO" ? props.label : props.label + " - " + props.priority}</span>
        <span className="value">{props.text}</span>
    </li>;
};

export const StatusDetails = props => {
    const headlineStatus = props.version && props.version.title ? props.version.title + " " + props.version.version + " - " + props.statusPage.status : props.statusPage.status;
    const componentItems = [];
    for ( const id of Object.keys(props.statusPage.components)) {
        const comp = props.statusPage.components[id];
        componentItems.push(<StatusComponent key={id} id={id} label={comp.label} priority={comp.priority} text={comp.text} />);
    }
    return (
        <div>
            <div className={ "status-page priority-" + props.statusPage.status.toLowerCase() }>
                <h1>{headlineStatus}</h1>
                <ul>{componentItems}</ul>
            </div>
            <Readiness readiness={props.readiness} />
        </div>
    );
};

export const LoadingStatusDetails = props => {
    if (!props.statusPage || !props.version || !props.readiness) {
        return <div>Loading...</div>;
    }
    return <div>
        <StatusDetails {...props} />
    </div>;
};

export const Readiness = ({readiness}) => (
    readiness !== null && readiness.toLowerCase() === "ready"
        ? <div className="readiness readiness-ready">Application ready</div>
        : <div className="readiness readiness-other">Application NOT ready</div>
);
