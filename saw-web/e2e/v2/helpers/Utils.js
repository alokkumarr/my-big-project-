'use strict';
class Utils {

  /* Returns object found in list by passed inputValue and inputKey.
 * For example if we have list like:
 * [{a: 1, b: 2},{a: 3, b :4}]
 * There are two items in list: {a: 1, b: 2} and {a: 3, b :4}
 * We want to find value(or object) in key 'b' by key-value `a: 1`
 * So we invoke function like getValueFromListByKeyValue(list, a, 1, b)
 *
 * Caution: inputValue should be unique because function takes returnValue from any matching inputKey-inputValue
 */
  getValueFromListByKeyValue(list, inputKey, inputValue, getValueOfKey) {
    let returnValue;

    for (let i = 0; i < list.length; i++) {
      const data = list[i];
      // Iterate each item in list
      // If inputValue matches, return value of getValueOfKey from this item in list
      Object.keys(data).forEach(function (key) {
        if (key === inputKey && data[key] === inputValue) {
          returnValue = data[getValueOfKey];
          //console.log('Found! ' + inputKey + ': \'' + data[key] + '\'');
        }
      });
    }
    if (returnValue == null) {
      throw new Error('There is no ' + inputKey + ' in list with value ' + inputValue);
    }
    return returnValue;
  }

}

module.exports = Utils;
