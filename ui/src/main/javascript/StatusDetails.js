import React from 'react';
import styled from 'styled-components';

export const StatusComponent = ({label, priority, text}) => (
    <ColouredListItem priority={priority}>
        <StatusComponentLabel>{priority === "INFO" ? label : label + " - " + priority}</StatusComponentLabel>
        <StatusComponentText>{text}</StatusComponentText>
    </ColouredListItem>
);

export const StatusDetails = props => {
    const headlineStatus = props.version && props.version.title ? props.version.title + " " + props.version.version + " - " + props.statusPage.status : props.statusPage.status;
    const componentItems = [];
    for ( const id of Object.keys(props.statusPage.components)) {
        const comp = props.statusPage.components[id];
        componentItems.push(<StatusComponent key={id} id={id} label={comp.label} priority={comp.priority} text={comp.text} />);
    }
    return (
        <div>
            <div>
                <ColouredHeadlineStatus priority={props.statusPage.status}>{headlineStatus}</ColouredHeadlineStatus>
                <StatusComponentList>{componentItems}</StatusComponentList>
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
    <ColouredReadiness readiness={readiness}>{readiness !== null && readiness.toLowerCase() === "ready" ? "Application ready" : "Application NOT ready"}</ColouredReadiness>
);

const priorityColours = {
    critical: { background: "#ce002c",   foreground: "white" },
    warning:  { background: "#eccd10",   foreground: "black" },
    ok:       { background: "#0da237",   foreground: "white" },
    info:     { background: "white",     foreground: "black" },
    unknown:  { background: "black",     foreground: "white" }
};

const readinessColours = {
    ready:       priorityColours.ok,
    "not_ready": priorityColours.warning,
    unknown:     priorityColours.unknown
};

const ColouredListItem = styled.li`
  background-color: ${({priority}) => (priorityColours[priority.toLowerCase()] || priorityColours.unknown).background};
  color: ${({priority}) => (priorityColours[priority.toLowerCase()] || priorityColours.unknown).foreground};
`;

const ColouredHeadlineStatus = styled.h1`
  background-color: ${({priority}) => (priorityColours[priority.toLowerCase()] || priorityColours.unknown).background};
  color: ${({priority}) => (priorityColours[priority.toLowerCase()] || priorityColours.unknown).foreground};
  padding: 8px;
`;

const ColouredReadiness = styled.div`
  background-color: ${({readiness}) => (readinessColours[readiness.toLowerCase()] || readinessColours.unknown).background};
  color: ${({readiness}) => (readinessColours[readiness.toLowerCase()] || readinessColours.unknown).foreground};
  padding: 8px;
`;

const StatusComponentLabel = styled.div`
  font-weight: bold;
`;

const StatusComponentText = styled.span`
`;

const StatusComponentList = styled.ul`
padding: 0;

> li {
  list-style-type: none;
  padding: 8px;
}
`;
