import React from 'react';
import ReactDOM from 'react-dom';
import Status from 'app/Status';
import Bus from 'app/Bus';
import css from 'app/status.css';

window.BUS = new Bus();
const elt = document.createElement("div");
elt.id = "component.Status";
document.body.insertBefore(elt, document.body.childNodes[0]);
ReactDOM.render(<Status/>, elt);
