'use strict';

const logger = require('../../../conf/logger')(__filename);
const commonFunctions = require('../../utils/commonFunctions');
const TestConnectivity = require('./TestConnectivity');

class ChannelModel extends TestConnectivity {
  constructor() {
    super();
    this._channelType1 = name => element(by.css(`[e2e="${name}"]`));
    this._channelType = name =>
      element(by.xpath(`//mat-card/descendant::*[text()="${name}"]`));

    this._channelNextButton = element(
      by.css(`[e2e="create-channel-next-button"]`)
    );
    this._channelNameInput = element(by.css(`[e2e="name-of-channel"]`));
    this._accesTypeSelect = element(by.css(`[e2e="access-type-select"]`));
    this._accessRead = element(by.css(`[e2e="access-r"]`));
    this._accessReadWrite = element(by.css(`[e2e="access-rw"]`));
    this._channelHostNameInput = element(by.css(`[e2e="host-name-input"]`));
    this._channelUserNameInput = element(by.css(`[e2e="name-of-user-input"]`));
    this._channelPortNumberInput = element(by.css(`[e2e="port-no-input"]`));
    this._channelPasswordInput = element(by.css(`[e2e="user-password"]`));
    this._showPasswordBtn = element(by.css(`[e2e="show-password-sc"]`));
    this._hidePasswordBtn = element(by.css(`[e2e="hide-password-cs"]`));
    this._channelDescTextArea = element(
      by.css(`[e2e="channel-description-input"]`)
    );
    this._channelPreviousBtn = element(by.css(`[e2e="channel-prev-btn"]`));
    this._channelCreateBtn = element(by.css(`[e2e="channel-create-btn"]`));
    this._channelUpdateBtn = element(by.css(`[e2e="channel-update-btn"]`));
    this._testConnectivity = element(
      by.css(`[e2e="channel-test-connect-btn-model"]`)
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
    browser.sleep(500);
    if (type == 'r') {
      commonFunctions.clickOnElement(this._accessRead);
    } else {
      commonFunctions.clickOnElement(this._accessReadWrite);
    }
  }

  enterHostName(hostName) {
    commonFunctions.fillInput(this._channelHostNameInput, hostName);
  }

  fillUserName(userName) {
    commonFunctions.fillInput(this._channelUserNameInput, userName);
  }

  fillPortNumber(portNumber) {
    commonFunctions.fillInput(this._channelPortNumberInput, portNumber);
  }

  fillPassword(password) {
    this._channelPasswordInput.clear().sendKeys(password);
  }

  fillDescription(description) {
    commonFunctions.fillInput(this._channelDescTextArea, description);
  }

  clickOnTestConnectivity() {
    commonFunctions.clickOnElement(this._testConnectivity);
    browser.sleep(5000);
  }

  clickOnPreviousButton() {
    commonFunctions.clickOnElement(this._channelPreviousBtn);
  }

  clickOnCreateButton() {
    commonFunctions.clickOnElement(this._channelCreateBtn);
  }
  clickOnUpdateChannel() {
    commonFunctions.clickOnElement(this._channelUpdateBtn);
  }
}

module.exports = ChannelModel;
