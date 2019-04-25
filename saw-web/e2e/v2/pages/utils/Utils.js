'use strict';
class Utils {
  static hasClass(element, cls) {
    return element
      .getAttribute('class')
      .then(classes => classes.split(' ').includes(cls));
  }

  static arrayContainsArray(superset, subset) {
    if (0 === subset.length) {
      return false;
    }
    return subset.every(function(value) {
      return superset.indexOf(value) >= 0;
    });
  }
}
module.exports = Utils;
