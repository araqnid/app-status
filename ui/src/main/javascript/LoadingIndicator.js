import React, {useEffect, useState} from "react";

export const LoadingIndicator = ({ loading, delayTime = 100 }) => {
    const [throbberVisible, setThrobberVisible] = useState(false);
    useEffect(() => {
        let timer = null;
        if (loading) {
            timer = setTimeout(() => {
                setThrobberVisible(true);
            }, delayTime);
        }
        else {
            setThrobberVisible(false);
        }
        return () => {
            if (timer) {
                clearTimeout(timer);
            }
            setThrobberVisible(false);
        };
    }, [loading]);
    if (throbberVisible) {
        return <div className="spinner-border" role="status"><span className="sr-only">Loading</span></div>;
    }
    else {
        return null;
    }
};
