var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var minify = require('gulp-clean-css');
var logger = require('gulp-logger');
var filesExist = require('files-exist');

gulp.task('js', function () {
    gulp.src(filesExist([
        'node_modules/jquery/dist/jquery.js',
        'node_modules/bootstrap/dist/js/bootstrap.js',
        'node_modules/handlebars/dist/handlebars.js',
        'node_modules/typeahead.js/dist/typeahead.bundle.js',
        'node_modules/googlediff/javascript/diff_match_patch_uncompressed.js',
        'node_modules/jquery-prettytextdiff/jquery.pretty-text-diff.js',
        'node_modules/fancybox/dist/js/jquery.fancybox.js',
        'node_modules/angular/angular.js',
        'node_modules/angular-resource/angular-resource.js',
        'node_modules/angular-route/angular-route.js',
        'node_modules/angular-ui-router/release/angular-ui-router.js',
        'js/template-feature-pasta.js'
    ]))
        .pipe(logger({
            before: '### Inicio - feature-pasta.min.js',
            after: '### Fim - feature-pasta.min.js',
            extname: '.js',
            showChange: true
        }))
        .pipe(concat('feature-pasta.min.js', {newLine: ";\r\n"}))
        .pipe(uglify())
        .pipe(gulp.dest('dist/'));

    gulp.src(filesExist([
        'js/app.js',
        'js/featureController.js'
    ]))
        .pipe(logger({
            before: '### Inicio - feature-pasta-angular.min.js',
            after: '### Fim - feature-pasta-angular.min.js',
            extname: '.js',
            showChange: true
        }))
        .pipe(concat('feature-pasta-angular.min.js', {newLine: ";\r\n"}))
        .pipe(uglify({mangle: false}))
        .pipe(gulp.dest('dist/'));
});

gulp.task('css', function () {
    gulp.src(filesExist([
        'node_modules/bootstrap/dist/css/bootstrap.css',
        'node_modules/fancybox/source/jquery.fancybox.css',
        'css/simple-sidebar.css',
        'css/feature-base.css',
        'css/template-feature-pasta.css'
    ]))
        .pipe(logger({
            before: '### Inicio - feature-pasta.min.css',
            after: '### Fim - feature-pasta.min.css',
            extname: '.css',
            showChange: true
        }))
        .pipe(concat('feature-pasta.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src(filesExist([
        'node_modules/bootstrap/dist/css/bootstrap.css',
        'css/feature-base.css',
        'css/template-feature.css'
    ]))
        .pipe(logger({
            before: '### Inicio - feature.min.css',
            after: '### Fim - feature.min.css',
            extname: '.css',
            showChange: true
        }))
        .pipe(concat('feature.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src(filesExist([
        'css/feature-base.css',
        'css/template-feature-pdf.css'
    ]))
        .pipe(logger({
            before: '### Inicio - feature-pdf.min.css',
            after: '### Fim - feature-pdf.min.css',
            extname: '.css',
            showChange: true
        }))
        .pipe(concat('feature-pdf.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));
});

gulp.task('default', ['js', 'css'], function () {});