//'use strict';
const commonFunctions = require('../utils/commonFunctions');
const ChannelModel = require('./components/ChannelModel');
const RouteModel = require('./components/RouteModel');
const TestConnectivity = require('./components/TestConnectivity');
const mixin = require('mixin-es6');

class WorkBenchPage extends mixin(ChannelModel, RouteModel, TestConnectivity) {
  constructor() {
    super();
    this._addChannelButton = element(by.css(`[e2e="add-new-channel-btn"]`));
    this._createdChannel = name => element(by.css(`[e2e="${name}"]`));

    this._createdChannelHostName = name =>
      element(by.xpath(`//*[@e2e="${name}"]/following::td[1]`));

    this._createdChannelActiveInactiveBtn = name =>
      element(
        by.xpath(
          `//*[@e2e="${name}"]/following::button[@e2e='channel-active-inactive']`
        )
      );
  }
  clickOnAddChannelButton() {
    commonFunctions.clickOnElement(this._addChannelButton);
  }

  verifyChannelInformationOnSourceListSection(name, host, status) {}

  verifyChannelInformationOnSourceDetailSection(name, d) {}
}

module.exports = WorkBenchPage;
