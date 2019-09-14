import Observable from "zen-observable";
import {asActions, concat} from "../../main/javascript/observables";

describe("concat", () => {
    it("concatenates the supplied Observable arguments", async () => {
        const list = [];

        await concat(Observable.of(1, 2, 3, 4), Observable.of(5, 6, 7, 8))
            .forEach(x => list.push(x));

        expect(list).toEqual([1, 2, 3, 4, 5, 6, 7, 8]);
    });

    it("can be used multiple times to produce the same results", async () => {
        const list1 = [];
        const list2 = [];

        await concat(Observable.of(1, 2, 3, 4), Observable.of(5, 6, 7, 8))
            .forEach(x => list1.push(x));
        await concat(Observable.of(1, 2, 3, 4), Observable.of(5, 6, 7, 8))
            .forEach(x => list2.push(x));

        expect(list1).toEqual([1, 2, 3, 4, 5, 6, 7, 8]);
        expect(list2).toEqual([1, 2, 3, 4, 5, 6, 7, 8]);
    });
});

describe("asActions", () => {
    it("converts emitted values to actions of the specified type", async () => {
        const list = [];

        await asActions("colour")(Observable.of("red", "blue"))
            .forEach(x => list.push(x));

        expect(list).toEqual([
            { type: "colour", payload: "red" },
            { type: "colour", payload: "blue" }
        ]);
    });

    it("converts terminal error to error action", async () => {
        const list = [];

        const produceError = new Observable(observer => {
            observer.next("value");
            observer.error(new Error("This is an error"));
        });

        await asActions("test")(produceError)
            .forEach(x => list.push(x));
        await asActions("test", "test_custom_error")(produceError)
            .forEach(x => list.push(x));

        expect(list).toEqual([
            { type: "test", payload: "value" },
            { type: "test.error", payload: new Error("This is an error"), error: true },
            { type: "test", payload: "value" },
            { type: "test_custom_error", payload: new Error("This is an error"), error: true }
        ]);
    });
});
