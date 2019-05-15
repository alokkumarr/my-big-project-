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

    this._channelAttributes = name =>
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
    this._getRouteScheduleRowValueOf = ColumnName =>
      element(
        by.xpath(`(//*[@e2e="route-log-${ColumnName}"])[position()=last()]`)
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
      this._channelAttributes('host-name'),
      channelInfo.sftpHost
    );
    commonFunctions.validateText(
      this._channelAttributes('access-type'),
      channelInfo.access
    );
    commonFunctions.validateText(
      this._channelAttributes('port-number'),
      channelInfo.sftpPort
    );
    commonFunctions.validateText(
      this._channelAttributes('created-by'),
      channelInfo.created
    );
    commonFunctions.validateText(
      this._channelAttributes('user-name'),
      channelInfo.sftpUser
    );
    commonFunctions.clickOnElement(this._showPwd);
    commonFunctions.waitFor.elementToBeVisible(this._hidePwd);
    commonFunctions.validateText(
      this._channelAttributes('password-name'),
      channelInfo.sftpPwd
    );
    commonFunctions.validateText(
      this._channelAttributes('description'),
      channelInfo.desc
    );
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
    browser.sleep(2000);
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

      element(
        _self
          ._getRouteScheduleRowValueOf('fileName')
          .isPresent()
          .then(present => {
            if (present) {
              _self
                ._getRouteScheduleRowValueOf('fileName')
                .getText()
                .then(text => {
                  if (text === `${routeInfo.source}/${routeInfo.fileName}`) {
                    expect(
                      _self._getRouteScheduleRowValueOf('fileName').getText()
                    ).toEqual(`${routeInfo.source}/${routeInfo.fileName}`);
                    _self.scheduleVerification(routeInfo);
                    _self.closeRouteLogModel();
                  } else {
                    _self.closeRouteLogModel();
                    console.log(
                      `Element present, Attempt:${index} done but content is the one which wee need`
                    );
                    console.log(`waiting for 20 seconds and check again`);
                    browser.sleep(20000);
                    process(index + 1);
                  }
                });
            } else {
              _self.closeRouteLogModel();
              console.log(`Element not present Attempt:${index} done`);
              console.log(`waiting for 20 seconds and check again`);
              browser.sleep(20000);
              process(index + 1);
            }
          })
      );
    })(1);
  }

  scheduleVerification(routeInfo) {
    expect(this._getRouteScheduleRowValueOf('filePattern').getText()).toEqual(
      routeInfo.filePattern
    );
    expect(this._getRouteScheduleRowValueOf('fileName').getText()).toContain(
      `${routeInfo.source}/${routeInfo.fileName}`
    );
    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('actualFileRecDate')
    );
    expect(
      this._getRouteScheduleRowValueOf('actualFileRecDate')
    ).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('recdFileName')
    );
    expect(
      this._getRouteScheduleRowValueOf('recdFileName').getText()
    ).toContain(routeInfo.destination);
    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('recdFileSize')
    );
    expect(this._getRouteScheduleRowValueOf('recdFileSize')).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('mflFileStatus')
    );
    expect(
      this._getRouteScheduleRowValueOf('mflFileStatus').getText()
    ).toContain('SUCCESS');

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('bisProcessState')
    );
    expect(
      this._getRouteScheduleRowValueOf('bisProcessState').getText()
    ).toContain('DATA_RECEIVED');

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('transferStartTime')
    );
    expect(
      this._getRouteScheduleRowValueOf('transferStartTime')
    ).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('transferEndTime')
    );
    expect(this._getRouteScheduleRowValueOf('transferEndTime')).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('transferDuration')
    );
    expect(this._getRouteScheduleRowValueOf('transferDuration')).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('modifiedDate')
    );
    expect(this._getRouteScheduleRowValueOf('modifiedDate')).not.toBeNull();

    commonFunctions.scrollIntoView(
      this._getRouteScheduleRowValueOf('createdDate')
    );
    expect(this._getRouteScheduleRowValueOf('createdDate')).not.toBeNull();
  }
}

module.exports = DataSourcesPage;
