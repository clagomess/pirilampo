var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var minify = require('gulp-clean-css');
var logger = require('gulp-logger');

gulp.task('js', function () {
    gulp.src([
        'bower_components/jquery/dist/jquery.js',
        'bower_components/bootstrap/dist/js/bootstrap.js',
        'bower_components/handlebars/handlebars.js',
        'bower_components/typeahead.js/dist/typeahead.bundle.js',
        'bower_components/google-diff-match-patch/javascript/diff_match_patch_uncompressed.js',
        'bower_components/jQuery.PrettyTextDiff/jquery.pretty-text-diff.js',
        'bower_components/fancybox/source/jquery.fancybox.js',
        'bower_components/angular/angular.js',
        'bower_components/angular-resource/angular-resource.js',
        'bower_components/angular-route/angular-route.js',
        'bower_components/angular-ui-router/release/angular-ui-router.js',
        'js/template-feature-pasta.js'
    ])
        .pipe(logger({
            before: '### Inicio - feature-pasta.min.js',
            after: '### Fim - feature-pasta.min.js',
            extname: '.js',
            showChange: true
        }))
        .pipe(concat('feature-pasta.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'js/app.js',
        'js/featureController.js'
    ])
        .pipe(logger({
            before: '### Inicio - feature-pasta-angular.min.js',
            after: '### Fim - feature-pasta-angular.min.js',
            extname: '.js',
            showChange: true
        }))
        .pipe(concat('feature-pasta-angular.min.js'))
        .pipe(uglify({mangle: false}))
        .pipe(gulp.dest('dist/'));
});

gulp.task('css', function () {
    gulp.src([
        'bower_components/bootstrap/dist/css/bootstrap.css',
        'bower_components/fancybox/source/jquery.fancybox.css',
        'css/simple-sidebar.css',
        'css/feature-base.css',
        'css/template-feature-pasta.css'
    ])
        .pipe(logger({
            before: '### Inicio - feature-pasta.min.css',
            after: '### Fim - feature-pasta.min.css',
            extname: '.css',
            showChange: true
        }))
        .pipe(concat('feature-pasta.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'bower_components/bootstrap/dist/css/bootstrap.css',
        'css/feature-base.css',
        'css/template-feature.css'
    ])
        .pipe(logger({
            before: '### Inicio - feature.min.css',
            after: '### Fim - feature.min.css',
            extname: '.css',
            showChange: true
        }))
        .pipe(concat('feature.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'css/feature-base.css',
        'css/template-feature-pdf.css'
    ])
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