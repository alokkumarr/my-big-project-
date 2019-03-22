'use strict';

const commonFunctions = require('../pages/utils/commonFunctions');

class PreviewPage {
  constructor() {
    this._axisTitleUpdated = (chartType, axisLabel, axis) =>
      element(
        by.xpath(
          `(//chart/descendant::*[name()="svg"])[position()=last()]/descendant::*[name()="g" and contains(@class,"highcharts-${axis}")]/descendant::*[contains(text(),"${axisLabel}")]`
        )
      );
  }

  verifyAxisTitle(chartType, axisLabel, axis) {
    commonFunctions.waitFor.elementToBeVisible(
      this._axisTitleUpdated(chartType, axisLabel, axis)
    );
  }
}
module.exports = PreviewPage;
