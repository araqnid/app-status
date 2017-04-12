define(['react', 'react-dom',
        'app/Status', 'app/Bus', 'app/status.css'],
function status$$init(React, ReactDOM,
                Status, Bus) {
    window.BUS = new Bus();
    const elt = document.createElement("div");
    elt.id = "component.Status";
    document.body.insertBefore(elt, document.body.childNodes[0]);
    ReactDOM.render(<Status/>, elt);
    return "status";
});
