#!/usr/bin/env bash

# npm install -g bower
# npm install -g gulp-cli
npm install gulp
npm install gulp-concat
npm install gulp-uglify
npm install gulp-clean-css
npm install gulp-logger
bower install
gulp
rm -rf bower_components
rm -rf node_modules