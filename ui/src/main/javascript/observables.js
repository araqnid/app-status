import Observable from "zen-observable";

export function concat(...sources) {
    return new Observable(observer => {
        let index = 0;
        let subscription = null;

        function subscribeNext() {
            if (subscription) throw new Error("subscribeNext called while subscription already active");
            if (index === sources.length) {
                observer.complete();
                return;
            }
            subscription = sources[index++].subscribe(
                value => {
                    observer.next(value);
                },
                error => {
                    observer.error(error);
                    subscription = null;
                },
                () => {
                    subscription = null;
                    subscribeNext();
                });
        }

        subscribeNext();

        return () => {
            if (subscription) subscription.unsubscribe();
        };
    })
}

export function asActions(type) {
    return underlying => {
        return new Observable(observer => {
            underlying.subscribe(
                value => { observer.next({ type: type, payload: value }) },
                error => { observer.next({ type: `${type}.error`, payload: error, error: true }); observer.complete() },
                () => { observer.complete() }
            );
        });
    };
}
