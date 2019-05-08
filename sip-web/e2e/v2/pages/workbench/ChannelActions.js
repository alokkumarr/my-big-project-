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
}

module.exports = ChannelActions;
