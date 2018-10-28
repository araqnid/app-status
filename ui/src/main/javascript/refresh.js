import Observable from "zen-observable";

export function timer(intervalMillis = 500) {
    const subscribers = new Set();
    let timer = null;

    function tick() {
        subscribers.forEach(observer => {
            observer.next(null);
        });
    }

    return new Observable(observer => {
        subscribers.add(observer);

        if (!timer) {
            timer = setInterval(tick, intervalMillis);
            tick();
        }

        return () => {
            subscribers.delete(observer);
            if (subscribers.size === 0) {
                clearInterval(timer);
            }
        }
    });
}

export function autoRefreshFromClock(clockObservable) {
    return dataObservable => {
        return new Observable(observer => {
            let dataSubscription = null;
            const clockSubscription = clockObservable.subscribe(
                () => {
                    if (!dataSubscription) {
                        dataSubscription = dataObservable.subscribe(
                            observer.next.bind(observer),
                            error => {
                                dataSubscription = null;
                                observer.error(error);
                            },
                            () => {
                                dataSubscription = null;
                            }
                        );
                    }
                }
            );

            return () => {
                clockSubscription.unsubscribe();
                if (dataSubscription)
                    dataSubscription.unsubscribe();
            }
        });
    }
}

export function autoRefresh(intervalMillis) {
    return autoRefreshFromClock(timer(intervalMillis));
}
