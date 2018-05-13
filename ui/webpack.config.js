const webpack = require("webpack");
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const assetsDir = path.resolve(__dirname, "src/main/javascript");

const production = process.env.NODE_ENV === "production";

module.exports = {
    context: assetsDir,
    entry: './main',
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
                        presets: [['env', { modules: false }], 'react'],
                        plugins: ["transform-object-rest-spread"]
                    }
                }
            },
            {
                test: /\.css$/,
                use: [ 'style-loader', 'css-loader' ]
            },
            {
                test: /\.(png|woff|woff2|eot|ttf|svg)$/,
                loader: 'url-loader?limit=100000'
            }
        ]
    },
    resolve: {
        extensions: ['.js', '.jsx']
    },
    plugins: [
        new HtmlWebpackPlugin({
            title: "Status"
        })
    ]
};

// Local Variables:
// compile-command: "node_modules/.bin/webpack -d"
// End:
