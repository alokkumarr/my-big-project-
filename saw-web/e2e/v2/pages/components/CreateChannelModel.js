'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');
const TestConnectivityWindow = require('./TestConnectivityWindow');

class CreateChannelModel extends TestConnectivityWindow {
  constructor() {
    super();

    this._channelType = name => element(by.css(`[ng-reflect-e2e="${name}"]`));
    this._channelNextButton = element(
      by.css(`[e2e="create-channel-next-button"]`)
    );
    this._channelNameInput = element(by.css(`[e2e="name-of-channel"]`));
    this._accesTypeSelect = element(by.css(`[e2e="access-type"]`));
    this._accessRead = element(by.css(`[e2e="access-r"]`));
    this._accessReadWrite = element(by.css(`[e2e="access-rw"]`));
    this._channelHostNameInput = element(by.css(`[e2e="host-name"]`));
    this._channelUserNameInput = element(by.css(`[e2e="name-of-user"]`));
    this._channelPortNumberInput = element(by.css(`[e2e="port-no"]`));
    this._channelPasswordInput = element(by.css(`[e2e="user-password"]`));
    this._showPasswordBtn = element(by.css(`[e2e="show-password"]`));
    this._hidePasswordBtn = element(by.css(`[e2e="hide-password"]`));
    this._channelDescTextArea = element(by.css(`[e2e="channel-description"]`));
    this._channelPreviousBtn = element(by.css(`[e2e="channel-prev-btn"]`));
    this._channelCreateBtn = element(by.css(`[e2e="channel-create-btn"]`));
    this._channelUpdateBtn = element(by.css(`[e2e="channel-update-btn"]`));
    this._testConnectivity = element(
      by.css(`[e2e="channel-test-connect-btn"]`)
    );
  }

  clickOnChannelType(name) {
    commonFunctions.clickOnElement(this._channelType(name));
  }

  clickOnChannelNextButton() {
    commonFunctions.clickOnElement(this._channelNextButton);
  }

  fillChannelName(channelName) {
    commonFunctions.fillInput(this._channelNameInput, channelName);
  }

  selectAccessType(type) {
    commonFunctions.clickOnElement(this._accesTypeSelect);
    if (type == 'read') {
      commonFunctions.clickOnElement(this._accessRead);
    } else {
      commonFunctions.clickOnElement(this._accessReadWrite);
    }
  }

  fillHostName(hostName) {
    commonFunctions.fillInput(this._channelHostNameInput, hostName);
  }

  fillUserName(userName) {
    commonFunctions.fillInput(this._channelUserNameInput, userName);
  }

  fillPortNumber(portNumber) {
    commonFunctions.fillInput(this._channelPortNumberInput, portNumber);
  }

  fillPassword(passowrd) {
    commonFunctions.fillInput((this._channelPasswordInput, passowrd));
  }

  fillDescription(description) {
    commonFunctions.fillInput(this._channelDescTextArea, description);
  }

  clickOnTestConnectivity() {
    commonFunctions.clickOnElement(this._testConnectivity);
  }

  clickOnPreviousButton() {
    commonFunctions.clickOnElement(this._channelPreviousBtn);
  }

  clickOnCreateButton() {
    commonFunctions.clickOnElement(this._channelNextButton);
  }
}

module.exports = CreateChannelModel;
