require('@babel/register')({
    presets: ["@babel/env", "@babel/react"],
    plugins: ["@babel/plugin-proposal-object-rest-spread"]
});
process.env.NODE_ENV = "test";