import React from "react";
import {mount} from "enzyme";
import MockAdapter from "axios-mock-adapter";
import App from "../../main/javascript/App";
import {localAxios} from "../../main/javascript/ajax";
import {Readiness} from "../../main/javascript/StatusDetails";
import Status from "../../main/javascript/Status";

let component = null;
let mockAxios = null;

beforeAll(() => {
    mockAxios = new MockAdapter(localAxios);
});

afterEach(() => {
    if (component) {
        component.unmount();
    }
    mockAxios.reset();
});

afterAll(() => {
    mockAxios.restore();
});

function mockStatus(status, readiness = "READY", version = {title: "test-app", version: "1.0.0", vendor: null}) {
    mockAxios.onGet("/_api/info/status").reply(200, status, {"Content-Type": "application/json"});
    mockAxios.onGet("/_api/info/readiness").reply(200, readiness, {"Content-Type": "text/plain"});
    mockAxios.onGet("/_api/info/version").reply(200, version, {"Content-Type": "application/json"});
}

it("loads status, version and readiness on mount", async () => {
    mockStatus({
        status: "OK",
        components: {jvmVersion: {label: "JVM version", priority: "INFO", text: "11.0.4"}}
    });
    component = mount(<App/>);

    expect(component.text()).toEqual("Loading");

    while (mockAxios.history.get.length < 3) {
        await timeoutTick();
    }
    component.update();

    expect(new Set(mockAxios.history.get.map(it => it.url))).toEqual(new Set(["/_api/info/status", "/_api/info/readiness", "/_api/info/version"]));
    expect(component.find("h1").text()).toBe("test-app 1.0.0 - OK");
    expect(component.find("#jvmVersion").text()).toContain("JVM version");
    expect(component.find("#jvmVersion").text()).toContain("11.0.4");
    expect(component.find(Readiness).text()).toContain("Application ready");
});

it("shows an error if status not available", async () => {
    component = mount(<App/>);

    while (mockAxios.history.get.length < 3) {
        await timeoutTick();
    }
    component.update();

    expect(new Set(mockAxios.history.get.map(it => it.url))).toEqual(new Set(["/_api/info/status", "/_api/info/readiness", "/_api/info/version"]));
    expect(component.find(Status).text()).toContain("Failed to load status");
    expect(component.find(Status).text()).toContain("Request failed with status code 404");
});

function timeoutTick(millis = 0) {
    return new Promise(resolve => {
        setTimeout(resolve, millis);
    });
}
