var gulp = require('gulp');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var minify = require('gulp-clean-css');

gulp.task('js', function () {
    gulp.src([
        'bower_components/jquery/dist/jquery.js',
        'bower_components/bootstrap/dist/js/bootstrap.js',
        'bower_components/handlebars/handlebars.js',
        'bower_components/typeahead.js/dist/typeahead.bundle.js',
        'bower_components/google-diff-match-patch/javascript/diff_match_patch_uncompressed.js',
        'bower_components/jQuery.PrettyTextDiff/jquery.pretty-text-diff.js',
        'bower_components/angular/angular.js',
        'bower_components/angular-resource/angular-resource.js',
        'bower_components/angular-route/angular-route.js',
        'bower_components/angular-ui-router/release/angular-ui-router.js',
        'js/template-feature-pasta.js'
    ])
        .pipe(concat('feature-pasta.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'js/app.js',
        'js/featureController.js'
    ])
        .pipe(concat('feature-pasta-angular.js'))
        .pipe(gulp.dest('dist/'));
});

gulp.task('css', function () {
    gulp.src([
        'bower_components/bootstrap/dist/css/bootstrap.css',
        'bower_components/lightbox2/dist/css/lightbox.css',
        'css/simple-sidebar.css',
        'css/feature-base.css',
        'css/template-feature-pasta.css'
    ])
        .pipe(concat('feature-pasta.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'bower_components/bootstrap/dist/css/bootstrap.css',
        'css/feature-base.css',
        'css/template-feature.css'
    ])
        .pipe(concat('feature.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));

    gulp.src([
        'bower_components/bootstrap/dist/css/bootstrap.css',
        'css/feature-base.css',
        'css/template-feature-pdf.css'
    ])
        .pipe(concat('feature-pdf.min.css'))
        .pipe(minify())
        .pipe(gulp.dest('dist/'));
});

gulp.task('default', ['js', 'css'], function () {});