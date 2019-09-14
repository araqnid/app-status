const webpack = require("webpack");
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const assetsDir = path.resolve(__dirname, "src/main/javascript");

const production = process.env.NODE_ENV === "production";

module.exports = {
    context: assetsDir,
    entry: './index',
    mode: production ? "production" : "development",
    output: {
        path: path.resolve(__dirname, 'build/site'),
        filename: production ? "[name]-[hash].js" : "[name].js"
    },
    devServer: {
        contentBase: assetsDir,
        port: 3000,
        proxy: { "*" : "http://localhost:8080" }
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: [['@babel/env', { modules: false }], '@babel/react'],
                        plugins: ["@babel/plugin-proposal-object-rest-spread",
                            ["babel-plugin-styled-components", {
                                displayName: !production
                            }]]
                    }
                }
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    plugins: [
        new HtmlWebpackPlugin({
            title: "Status",
            template: "../index.html.ejs"
        })
    ]
};

// Local Variables:
// compile-command: "node_modules/.bin/webpack -d"
// End:
