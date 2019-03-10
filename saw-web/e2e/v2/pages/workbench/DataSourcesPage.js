'use strict';
const commonFunctions = require('../utils/commonFunctions');
const DeleteModel = require('../workbench/components/DeleteModel');

class DataSourcesPage extends DeleteModel {
  constructor() {
    super();
    this._addChannelButton = element(by.css(`[e2e="add-new-channel-btn"]`));
    this._createdChannel = name => element(by.css(`[e2e="${name}"]`));

    this._createdChannelHostName = name =>
      element(by.xpath(`//*[@e2e="${name}"]/following::td[1]`));

    this._channelActivateDeactivate = (name, status) =>
      element(
        by.xpath(`//*[@e2e="${name}"]/following::button[@e2e="${status}"][1]`)
      );

    this._channelNameDetail = element(
      by.css(`[e2e="channel-name-detail-panel"]`)
    );
    this._showPwd = element(by.css(`[e2e="show-password-ds"]`));
    this._hidePwd = element(by.css(`[e2e="hide-password-ds"]`));

    this._items = name =>
      element(by.xpath(`//*[@e2e="${name}"]/following::span[1]`));

    this._channelDeleteBtn = element(by.css(`[e2e="delete-channel"]`));
    this._editChannel = element(by.css(`[e2e="edit-channel"]`));
    this._addRouteBtn = element(by.css(`[e2e="add-route"]`));
    this._routeItems = item => element(by.xpath(`//*[text()="${item}"]`));
    this._routeAction = name => element(by.css(`[e2e="${name}"]`));
    this._deleteRoute = element(by.css(`[e2e="delete-route-btn"]`));
  }

  clickOnAddChannelButton() {
    commonFunctions.clickOnElement(this._addChannelButton);
  }

  clickOnCreatedChannelName(name) {
    commonFunctions.clickOnElement(this._createdChannel(name));
  }

  verifyChannelDetailsInListView(channelName, hostName, status) {
    commonFunctions.waitFor.elementToBeVisible(
      this._createdChannel(channelName)
    );
    expect(this._createdChannelHostName(channelName).getText()).toEqual(
      hostName
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._channelActivateDeactivate(channelName, status)
    );
  }

  /**
   *
   * @param {Object} channelInfo
   * contains all the property which are displayed in current displayed channel
   */
  verifyCurrentDisplayedChannel(channelInfo) {
    commonFunctions.waitFor.elementToBeVisible(this._channelNameDetail);
    commonFunctions.validateText(
      this._channelNameDetail,
      channelInfo.channelName
    );
    commonFunctions.validateText(
      this._items('host-name'),
      channelInfo.sftpHost
    );
    commonFunctions.validateText(
      this._items('access-type'),
      channelInfo.access
    );
    commonFunctions.validateText(
      this._items('port-number'),
      channelInfo.sftpPort
    );
    commonFunctions.validateText(
      this._items('created-by'),
      channelInfo.created
    );
    commonFunctions.validateText(
      this._items('user-name'),
      channelInfo.sftpUser
    );
    commonFunctions.clickOnElement(this._showPwd);
    commonFunctions.waitFor.elementToBeVisible(this._hidePwd);
    commonFunctions.validateText(
      this._items('password-name'),
      channelInfo.sftpPwd
    );
    commonFunctions.validateText(this._items('description'), channelInfo.desc);
  }

  clickOnDeleteChannel() {
    commonFunctions.clickOnElement(this._channelDeleteBtn);
  }

  verifyChannelNotDeleted(name) {
    commonFunctions.waitFor.elementToBeNotVisible(this._createdChannel(name));
  }

  clickOnEditChannel() {
    commonFunctions.clickOnElement(this._editChannel);
  }

  deActivateChannel(name) {
    commonFunctions.clickOnElement(this._channelActivateDeactivate(name, 1));
    commonFunctions.waitFor.elementToBeVisible(
      this._channelActivateDeactivate(name, 0)
    );
  }

  clickOnAddRoute() {
    commonFunctions.clickOnElement(this._addRouteBtn);
  }

  clickOnRouteAction(name) {
    commonFunctions.clickOnElement(this._routeAction(name));
  }
  clickOnDeleteRoute() {
    commonFunctions.clickOnElement(this._deleteRoute);
  }

  verifyRouteDeleted(routeName) {
    commonFunctions.waitFor.elementToBeNotVisible(this._routeItems(routeName));
    browser.sleep(200);
  }
  verifyRouteDetails(routeInfo) {
    commonFunctions.waitFor.elementToBeVisible(
      this._routeItems(routeInfo.routeName)
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._routeItems(routeInfo.source)
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._routeItems(routeInfo.filePattern)
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._routeItems(routeInfo.destination)
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._routeItems(routeInfo.desc)
    );
  }
}

module.exports = DataSourcesPage;
