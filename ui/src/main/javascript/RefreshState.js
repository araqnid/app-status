import React from 'react';
import styled from 'styled-components';

export const RefreshState = ({ paused, interval, controls}) => {
    if (paused) {
        return <Styled><button onClick={controls.kick}>Refresh</button><button onClick={controls.unpause}>Start auto-refresh</button></Styled>;
    }
    else {
        return <Styled>Auto-refresh interval: {interval} <button onClick={controls.pause}>Stop auto-refresh</button></Styled>;
    }
};

const Styled = styled.div`
    border-top: solid 1px #888888;
    padding-top: 2ex;
    margin-top: 2ex;
`;
