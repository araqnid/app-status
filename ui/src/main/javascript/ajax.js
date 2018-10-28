import axios from "axios";
import Observable from "zen-observable";

const localAxios = axios.create({ headers: { "Accept": "application/json", "X-Requested-With": "XMLHttpRequest" }});

export function get(url, requestConfig = {}) {
    return new Observable(observer => {
        const cancelSource = axios.CancelToken.source();

        localAxios.get(url, { ...requestConfig, cancelToken: cancelSource.token }).then(
            (response) => {
                observer.next(response.data);
                observer.complete();
            },
            (err) => {
                observer.error(err);
            });

        return () => {
            cancelSource.cancel();
        }
    });
}
