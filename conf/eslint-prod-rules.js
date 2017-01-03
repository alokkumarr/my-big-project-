module.exports = {
  extends: [
    'angular'
  ],
  globals: {
    __DEVELOPMENT__: true
  },
  parserOptions: {
    sourceType: 'module'
  },
  rules: {
    'angular/no-service-method': 2,
    'no-negated-condition': 0,
    'quote-props': [1, 'as-needed'],
    'padded-blocks': 0,
    // setting this to max-params to 0, so it's disabled does not work because of eslints wierd merging method
    // the original rule form the angular plugin is set to ['error'] 3,
    // but that is not viable for angular dependency injection
    'max-params': ['error', 20],
    'no-warning-comments': [0]
  }
};
