var webpack = require("webpack");
var path = require('path');
var HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    entry: './src/main/web/main.jsx',
    output: {
        filename: 'bundle.js',
        path: path.resolve(__dirname, 'build/site')
    },
    resolve: {
        alias: {
            app: path.resolve(__dirname, 'src/main/web')
        },
        extensions: ['.js', '.jsx']
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /(node_modules|bower_components)/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['es2015', 'react']
                    }
                }
            },
            {
                test: /\.css$/,
                use: [ 'style-loader', 'css-loader' ]
            }
        ]
    },
    plugins: [
        new webpack.ProvidePlugin({
            // needed by bootstrap (react-bootstrap should allow to drop this?)
            jQuery: "jquery",
            $: "jquery",
            "window.jQuery": "jquery"
        }),
        new HtmlWebpackPlugin({
            title: "Status"
        })
    ]
};
