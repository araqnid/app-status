import MockAdapter from "axios-mock-adapter";
import * as ajax from "../../main/javascript/ajax";
import {localAxios} from "../../main/javascript/ajax";
import {asActions} from "../../main/javascript/observables";

let mockAxios = null;

beforeAll(() => {
    mockAxios = new MockAdapter(localAxios);
});

afterEach(() => {
    mockAxios.reset();
});

afterAll(() => {
    mockAxios.restore();
});

describe("get", () => {
    it("gets from API and returns response data", async () => {
        mockAxios.onGet("/test").reply(200, { data: "xyzzy" });
        const actions = [];
        await asActions("fetch")(ajax.get("/test")).forEach(action => actions.push(action));
        expect(actions).toEqual([
            { type: "fetch", payload: { data: "xyzzy" } }
        ]);
    });

    it("gets from API and propagates error", async () => {
        mockAxios.onGet("/test").reply(500);
        const actions = [];
        await asActions("fetch")(ajax.get("/test"))
            .map(action => action.error ? ({ type: action.type, error: true, payload: action.payload.response.status }) : action)
            .forEach(action => actions.push(action));
        expect(actions).toEqual([
            { type: "fetch.error", payload: 500, error: true }
        ]);
    });
});
