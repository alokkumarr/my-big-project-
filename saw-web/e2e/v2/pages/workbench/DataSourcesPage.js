'use strict';
const commonFunctions = require('../utils/commonFunctions');
const DeleteModel = require('../workbench/components/DeleteModel');
const LogHistoryModel = require('../workbench/components/LogHistoryModel');

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
    this._routeStatusBtn = element(by.css(`[e2e="route-active-inactive-btn"]`));
    this._routeStatus = status =>
      element(
        by.xpath(
          `//button[@e2e="route-active-inactive-btn"]/descendant::span[contains(text(),"${status}")]`
        )
      );
    this._back = element(
      by.xpath(`//div[contains(@class,"cdk-overlay-backdrop")]`)
    );
    this._routeSearchInput = element(
      by.xpath(`(//input[@aria-label="Search in data grid"])[2]`)
    );
    this._routeLogs = element(by.css(`[e2e="view-route-logs-btn"]`));
    this._routeScheduleRowColumn = colNum =>
      element(
        by.xpath(
          `((//*[@e2e="route-logs-container"]/descendant::tr)[position()=last()-1]/descendant::td)[position()=${colNum}]`
        )
      );

    this._closeRouteLogsModel = element(
      by.xpath(`//button[contains(@class,"close-button")]`)
    );
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
    browser.sleep(2000); // some time takes time to load
  }
  clickOnDeleteRoute() {
    commonFunctions.clickOnElement(this._deleteRoute);
  }

  verifyRouteDeleted(routeName) {
    browser.sleep(2000);
    commonFunctions.waitFor.elementToBeNotVisible(this._routeItems(routeName));
  }

  closeRouteLogModel() {
    commonFunctions.clickOnElement(this._closeRouteLogsModel);
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

  clickOnActivateDeActiveRoute() {
    commonFunctions.clickOnElement(this._routeStatusBtn);
  }
  verifyRouteStatus(status) {
    commonFunctions.waitFor.elementToBeVisible(this._routeStatus(status));
  }
  clickOnViewRouteLogs() {
    commonFunctions.clickOnElement(this._routeLogs);
  }

  verifyRouteScheduleInformation(routeInfo) {
    let _self = this;
    let attempts = 15;
    (function process(index) {
      if (index >= attempts) {
        return;
      }

      _self.clickOnRouteAction(routeInfo.routeName);
      _self.clickOnViewRouteLogs();
      browser.sleep(2000);
      commonFunctions.waitFor.elementToBeVisible(
        _self._routeScheduleRowColumn(2)
      );
      element(
        _self
          ._routeScheduleRowColumn(2)
          .getText()
          .then(text => {
            if (text === `${routeInfo.source}/${routeInfo.fileName}`) {
              expect(_self._routeScheduleRowColumn(2).getText()).toEqual(
                `${routeInfo.source}/${routeInfo.fileName}`
              );
              _self.scheduleVerification(routeInfo);
              _self.closeRouteLogModel();
            } else {
              _self.closeRouteLogModel();
              console.log(`Attempt:${index} done`);
              console.log(`waiting for 20 seconds and check again`);
              browser.sleep(20000);
              process(index + 1);
            }
          })
      );
    })(1);
  }

  scheduleVerification(routeInfo) {
    expect(this._routeScheduleRowColumn(1).getText()).toEqual(
      routeInfo.filePattern
    );
    expect(this._routeScheduleRowColumn(2).getText()).toContain(
      `${routeInfo.source}/${routeInfo.fileName}`
    );
    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(3));
    expect(this._routeScheduleRowColumn(3)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(4));
    expect(this._routeScheduleRowColumn(4).getText()).toContain(
      routeInfo.destination
    );
    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(5));
    expect(this._routeScheduleRowColumn(5)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(6));
    expect(this._routeScheduleRowColumn(6).getText()).toContain('SUCCESS');

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(7));
    expect(this._routeScheduleRowColumn(7).getText()).toContain(
      'DATA_RECEIVED'
    );

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(8));
    expect(this._routeScheduleRowColumn(8)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(9));
    expect(this._routeScheduleRowColumn(9)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(10));
    expect(this._routeScheduleRowColumn(10)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(11));
    expect(this._routeScheduleRowColumn(11)).not.toBeNull();

    commonFunctions.scrollIntoView(this._routeScheduleRowColumn(12));
    expect(this._routeScheduleRowColumn(12)).not.toBeNull();
  }
}

module.exports = DataSourcesPage;
