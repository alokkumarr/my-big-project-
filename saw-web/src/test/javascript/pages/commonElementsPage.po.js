const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');

module.exports = {
  errorToast: element(by.xpath("//*[@class='toast toast-error']")),
  toastDetailedError: element(by.xpath('//div[@class="error-detail-container"]/code')),
  ifErrorPrintTextAndFailTest: () => {
    ifErrorPrintTextAndFailTest();
  }
};

function ifErrorPrintTextAndFailTest() {
  // Click to see details
  module.exports.errorToast.click().then(
    function () {
      commonFunctions.waitFor.elementToBeVisible(module.exports.toastDetailedError);
      // Get detailed error and fail test
      module.exports.toastDetailedError.getText().then(function (errorText) {
        throw new Error("Error discovered with text: " + errorText);
      });
    }, function (err) {
      // If error not found - do nothing
    });
}
