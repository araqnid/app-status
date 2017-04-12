define(['jquery', 'react', 'react-dom',
        'app/Status', 'app/Bus', 'app/status.css',
        'bootstrap'],
function status$$init($, React, ReactDOM,
                Status, Bus) {
    window.BUS = new Bus();
    const $elt = $("<div id='component.Status'></div>");
    $elt.appendTo(document.body);
    const elt = $elt[0];
    ReactDOM.render(<Status/>, elt);
    return "status";
});
