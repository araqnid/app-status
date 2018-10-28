import React from "react";
import styled from "styled-components";

const Styled = styled.div`
float: right;
position: absolute;
right: 4px;
top: 4px;
background-color: white;
display: ${({loading}) => loading ? "block" : "none"};
`;

export const LoadingIndicator = ({ loading }) => (
    <Styled loading={loading}>{ loading ? "Loading" : "Quiet" }</Styled>
);
