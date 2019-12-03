'use strict';
const commonFunctions = require('../utils/commonFunctions');
const ChannelModel = require('./components/ChannelModel');

class ChannelActions extends ChannelModel {
  constructor() {
    super();
    // all the page elements in the channel list component
  }

  createNewChannel(channelInfo) {
    this.clickOnChannelType(channelInfo.sourceType);
    this.clickOnChannelNextButton();
    this.fillChannelName(channelInfo.channelName);
    this.selectAccessType(channelInfo.access);
    this.enterHostName(channelInfo.sftpHost);
    this.fillUserName(channelInfo.sftpUser);
    this.fillPortNumber(channelInfo.sftpPort);
    this.fillPassword(channelInfo.sftpPwd);
    this.fillDescription(channelInfo.desc);
    this.clickOnCreateButton();
  }

  fillApiChannleInfo(channelInfo, update = false) {
    this.fillChannelName(channelInfo.channelName);
    this.fillHostName(channelInfo.hostName);
    if (channelInfo.port) channelActions.fillPortNumber(channelInfo.port);
    this.selectMethodType(channelInfo.method);
    this.fillEndPoint(channelInfo.endPoint);
    if (channelInfo.method === 'POST') this.fillRequestBody(JSON.stringify(channelInfo.body));
    if (channelInfo.headers) {
      if (update) this.clearHeader();
      this.addHeaders(channelInfo.headers);
    }
    if (channelInfo.queryParams) {
      if (update) this.clearQueryParams();
      this.addQueryParams(channelInfo.queryParams);
    }
    this.fillDescription(channelInfo.desc);
  }

  testAndVerifyTestConnectivity(testConnectivityMessage) {
    this.clickOnTestConnectivity();
    this.verifyTestConnectivityLogs(testConnectivityMessage);
    this.closeTestConnectivity();
  }
}

module.exports = ChannelActions;
