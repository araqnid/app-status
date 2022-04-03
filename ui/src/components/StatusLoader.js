import { ajax } from "rxjs/ajax"
import { map, merge, of, concat, Observable } from "rxjs"

function fetchResource(type, responseType) {
    return ajax({
        url: `/_api/info/${type}`,
        headers: { accept: responseType === "text" ? "text/plain" : "application/json" },
        responseType
    })
    .pipe(
        map(response => ({type, payload: response.response}))
    )
}

function autoRefresh(refreshInterval) {
    if (!refreshInterval) return underlying => underlying
    return underlying => new Observable(observer => {
        let timer
        let subscription
        function kick() {
            subscription = underlying.subscribe({
                next(value) {
                    observer.next(value)
                },
                error(err) {
                    observer.error(err)
                },
                complete() {
                    subscription = undefined
                    awaitNextTick()
                }
            })
        }
        function awaitNextTick() {
            timer = setTimeout(() => {
                timer = undefined
                kick()
            }, refreshInterval)
        }
        kick()
        return () => {
            if (subscription) subscription.unsubscribe()
            if (timer) clearTimeout(timer)
        }
    })
}

export function statusLoader(refreshInterval) {
    return autoRefresh(refreshInterval)(
        concat(
                of({type: "refresh-start"}),
                merge(
                    fetchResource("status", "json"),
                    fetchResource("version", "json"),
                    fetchResource("readiness", "text"),
                ),
                of({type: "refresh-complete"}),
            )
    )
}
