import React, {useEffect, useMemo, useRef, useState} from 'react';
import Head from 'next/head'
import {statusLoader} from '../components/StatusLoader'
import Status from '../components/Status'
import LoadingIndicator from '../components/LoadingIndicator'

export default function IndexPage() {
    const [paused, setPaused] = useState(false)
    const [refreshInterval, setRefreshInterval] = useState(500)
    const [status, setStatus] = useState(null)
    const [version, setVersion] = useState(null)
    const [readiness, setReadiness] = useState(null)
    const [loadingError, setLoadingError] = useState(null)
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        if (!paused) {
            const subscription = statusLoader(refreshInterval).subscribe({
                next({type, payload}) {
                    switch (type) {
                        case "status":
                            setStatus(payload)
                            break;
                        case "version":
                            setVersion(payload)
                            break;
                        case "readiness":
                            setReadiness(payload)
                            break;
                        case "refresh-start":
                            setLoading(true)
                            break;
                        case "refresh-complete":
                            setLoading(false)
                            break;
                    }
                },
                error(err) {
                    setLoadingError(err)
                    setLoading(false)
                },
                complete() {
                    setLoading(false)
                }
            })
            return () => {
                subscription.unsubscribe()
            }
        }
    }, [paused, refreshInterval])

    const controls = useMemo(() => ({
        pause() {
            setPaused(true)
        },
        unpause() {
            setRefreshInterval(500)
            setPaused(false)
        },
        kick() {
            setPaused(true)
            setRefreshInterval(0)
            setTimeout(() => {
                setPaused(false)
            }, 0)
        },
        updateRefreshInterval(newInterval) {
            setRefreshInterval(newInterval)
        },
    }), [])

    return (
        <main>
            <Head>
                <title>Status</title>
            </Head>

            <section>
                <Status loadingError={loadingError}
                        values={{status, version, readiness}}
                        refresh={{paused, interval: refreshInterval}}
                        controls={controls}/>
                <LoadingIndicator loading={loading ? "true" : undefined}/>
            </section>
        </main>
    )
}
