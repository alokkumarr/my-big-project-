class FileUploadDirective {
  constructor() {
    this.restrict = 'A';
    this.require = '?ngModel';
    this.fileTypeRegex = /^file$/i;
  }

  link(scope, element, attrs, ngModel) {
    /* eslint-disable */
    if (ngModel && element[0].tagName === 'INPUT' && this.fileTypeRegex.test(attrs['type'])) {
      element.on('change', function () {
        var input = this;
        if ('multiple' in attrs) {
          var files = Array.prototype.map.call(input.files, function (file) { return file; });
          ngModel.$setViewValue(files);
        } else {
          ngModel.$setViewValue(input.files[0]);
        }
      });
    }
    /* eslint-enable */
  }
}

export default () => {
  return new FileUploadDirective();
};
