import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';

const elt = document.createElement("div");
elt.id = "app-status";
document.body.insertBefore(elt, document.body.childNodes[0]);
ReactDOM.render(<App/>, elt);
