import Observable from "zen-observable";
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

        expect(values).toEqual([0]);

        await delay(testInterval * 1.2);

        expect(values.length).toBeGreaterThan(1);
        expect(values[1]).toBe(1);
    });
});

describe("autoRefresh", () => {
    it("folds in underlying observable on each tick", async () => {
        const values = [];

        const testInterval = 10;

        subscription = autoRefresh(testInterval)(Observable.of("red", "blue")).subscribe(
            value => values.push(value)
        );

        await delay(testInterval / 2);

        expect(values).toEqual(["red", "blue"]);

        await delay(testInterval * 1.2);

        expect(values.length).toBeGreaterThan(2);
        expect(values[2]).toBe("red");
        expect(values[3]).toBe("blue");
    });
});

function delay(delayMillis) {
    return new Promise(resolve => {
        setTimeout(resolve, delayMillis);
    });
}
