'use strict';
class Page {
  constructor() {
  }
  open(path) {
    browser.get('/' + path);
  }
}
module.exports = Page;
