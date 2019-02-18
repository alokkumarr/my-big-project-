'use strict';
class Utils {
  static hasClass(element, cls) {
    return element
      .getAttribute('class')
      .then(classes => classes.split(' ').includes(cls));
  }
}
module.exports = Utils;
