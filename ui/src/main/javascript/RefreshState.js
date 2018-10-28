import React from 'react';
import styled from 'styled-components';

export const RefreshState = ({ paused, interval, store_kick, store_pause, store_unpause}) => {
    if (paused) {
        return <Styled><button onClick={store_kick}>Refresh</button><button onClick={store_unpause}>Start auto-refresh</button></Styled>;
    }
    else {
        return <Styled>Auto-refresh interval: {interval} <button onClick={store_pause}>Stop auto-refresh</button></Styled>;
    }
};

const Styled = styled.div`
    border-top: solid 1px #888888;
    padding-top: 2ex;
    margin-top: 2ex;
`;
