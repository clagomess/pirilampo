const { src, dest, parallel, series, watch } = require('gulp');
const concat = require('gulp-concat');
const filesExist = require('files-exist');

function jsFeaturePasta(){
    return src(filesExist([
        'node_modules/jquery/dist/jquery.min.js',
        'node_modules/bootstrap/dist/js/bootstrap.min.js',
        'node_modules/handlebars/dist/handlebars.min.js',
        'node_modules/typeahead.js/dist/typeahead.bundle.min.js',
        'node_modules/googlediff/javascript/diff_match_patch.js',
        'node_modules/jquery-prettytextdiff/jquery.pretty-text-diff.min.js',
        'node_modules/fancybox/dist/js/jquery.fancybox.pack.js',
        'node_modules/angular/angular.min.js',
        'node_modules/angular-resource/angular-resource.min.js',
        'node_modules/angular-route/angular-route.min.js',
        'node_modules/angular-ui-router/release/angular-ui-router.min.js',
        'js/template-feature-pasta.js'
    ]))
        .pipe(concat('feature-pasta.min.js', {newLine: ";\r\n"}))
        .pipe(dest('dist'));
}

function jsFeaturePastaAngular(){
    return src(filesExist([
        'js/app.js',
        'js/featureController.js'
    ]))
        .pipe(concat('feature-pasta-angular.min.js', {newLine: ";\r\n"}))
        .pipe(dest('dist'));
}

function cssFeaturePasta(){
    return src(filesExist([
        'node_modules/bootstrap/dist/css/bootstrap.min.css',
        'node_modules/fancybox/dist/css/jquery.fancybox.css',
        'css/simple-sidebar.css',
        'css/feature-base.css',
        'css/template-feature-pasta.css'
    ]))
        .pipe(concat('feature-pasta.min.css', {newLine: "\r\n\r\n"}))
        .pipe(dest('dist'));
}

function cssFeature(){
    src(filesExist([
        'node_modules/bootstrap/dist/css/bootstrap.min.css',
        'css/feature-base.css',
        'css/template-feature.css'
    ]))
        .pipe(concat('feature.min.css'))
        .pipe(dest('dist'));
}

function cssFeaturePdf(){
    src(filesExist([
        'css/feature-base.css',
        'css/template-feature-pdf.css'
    ]))
        .pipe(concat('feature-pdf.min.css'))
        .pipe(dest('dist'));
}

exports.default = parallel(
    jsFeaturePasta,
    jsFeaturePastaAngular,
    cssFeaturePasta,
    cssFeature,
    cssFeaturePdf,
);

