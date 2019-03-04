'use strict';
const commonFunctions = require('../utils/commonFunctions');

class WorkBenchPage {
  constructor() {
    this._addChannelButton = element(by.css(`[e2e="add-new-channel-btn"]`));
  }

  clickOnAddChannelButton() {
    commonFunctions.clickOnElement(this._addChannelButton);
  }
}
module.exports = WorkBenchPage;
