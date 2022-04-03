const StatusComponent = ({id, label, priority, text}) => (
    <PriorityAlert id={id} priority={priority}>
        <div className="label" style={{font: "bold"}}>{priority === "INFO" ? label : label + " - " + priority}</div>
        <span className="value">{text}</span>
    </PriorityAlert>
)

const Readiness = ({readiness}) => (
    <PriorityAlert id="_readiness" priority={readinessToPriority[readiness.toLowerCase()]}>
        {readiness !== null && readiness.toLowerCase() === "ready" ? "Application ready" : "Application NOT ready"}
    </PriorityAlert>
)

const PriorityAlert = ({id, priority, children}) => (
    <div id={id} data-priority={priority.toUpperCase()} className={"alert " + priorityClasses[priority.toLowerCase()]}>
        {children}
    </div>
)

const priorityClasses = {
    critical: "alert-danger",
    warning: "alert-warning",
    ok: "alert-success",
    info: "",
    unknown: ""
}

const readinessToPriority = {
    ready: "ok",
    "not_ready": "warning",
    unknown: "unknown"
}

const StatusDetails = ({version, status: {status: headlineStatus, components}, readiness}) => {
    const headline = version && version.title ? `${version.title} ${version.version} - ${headlineStatus}` : headlineStatus;
    return (
        <div>
            <PriorityAlert id="_headline" priority={headlineStatus}>{headline}</PriorityAlert>
            <Readiness readiness={readiness}/>
            <div style={{paddingLeft: "2em"}}>{Object.entries(components).map(([id, comp]) => (
                <StatusComponent key={id} id={id} label={comp.label} priority={comp.priority} text={comp.text}/>
            ))}</div>
        </div>
    )
}

export default StatusDetails
