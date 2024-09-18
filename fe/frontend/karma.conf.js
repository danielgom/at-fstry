module.exports = function (config) {
  config.set({
    // ... other configurations ...
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma') // Make sure this is present
    ],
    browsers: ['ChromeHeadless'],
  });
}
