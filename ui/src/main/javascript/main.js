import React from 'react';
import ReactDOM from 'react-dom';
import Status from './Status';
import "./status.css";

const elt = document.createElement("div");
elt.id = "component.Status";
document.body.insertBefore(elt, document.body.childNodes[0]);
ReactDOM.render(<Status/>, elt);
