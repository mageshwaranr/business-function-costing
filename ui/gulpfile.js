var gulp = require('gulp');
var sass = require('gulp-sass');
var minify = require('gulp-minify');

gulp.task('sassToCss', function() {
   gulp .src('./src/sass/*.scss', {base: './src/sass'})
        .pipe(sass())
        .pipe(gulp.dest('./dist/css/'));
});

gulp.task('minifyJs', function() {
    gulp .src('./src/js/*.js', {base: './src/js'})
        .pipe(minify())
        .pipe(gulp.dest('./dist/js/'));
});

gulp.task('watch', function() {
    gulp.watch('src/js/*.js', ['minifyJs']);
    gulp.watch('src/sass/*.scss', ['sassToCss']);
});

gulp.task('images', function() {
    gulp.src('./src/images/**/*')
    .pipe(gulp.dest('./dist/images'));
});

gulp.task('default', ['sassToCss', 'minifyJs','images']);