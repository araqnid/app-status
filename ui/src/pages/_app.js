import Head from 'next/head'
import 'bootstrap/dist/css/bootstrap.css'

export default function MyApp({Component, pageProps}) {
    return (
        <>
            <Head>
                <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
            </Head>
            <Component {...pageProps} />
        </>
    )
}
