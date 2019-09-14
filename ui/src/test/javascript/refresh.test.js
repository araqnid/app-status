import Observable from "zen-observable";
import assert from 'assert';
import {autoRefresh, timer} from "../../main/javascript/refresh";

let subscription = null;

afterEach(() => {
    if (subscription) {
        subscription.unsubscribe();
        subscription = null;
    }
});

describe("timer", () => {
    it("emits values starting immediately", async () => {
        const values = [];
        let counter = 0;

        const testInterval = 10;

        subscription = timer(testInterval).map(x => counter++).subscribe(
            value => values.push(value)
        );

        await delay(testInterval / 2);

        assert.deepStrictEqual(values, [0]);

        await delay(testInterval * 1.2);

        assert.deepStrictEqual(values, [0, 1]);
    });
});

describe("autoRefresh", () => {
    it("folds in underlying observable on each tick", async () => {
        const values = [];
        let counter = 0;

        const testInterval = 10;

        subscription = autoRefresh(testInterval)(Observable.of("red", "blue")).subscribe(
            value => values.push(value)
        );

        await delay(testInterval / 2);

        assert.deepStrictEqual(values, ["red", "blue"]);

        await delay(testInterval * 1.2);

        assert.deepStrictEqual(values, ["red", "blue", "red", "blue"]);
    });
});

function delay(delayMillis) {
    return new Promise(resolve => {
        setTimeout(resolve, delayMillis);
    });
}
