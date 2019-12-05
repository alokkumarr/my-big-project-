'use strict';
const commonFunctions = require('../utils/commonFunctions');
const DeleteModel = require('../workbench/components/DeleteModel');
const Header = require('../../pages/components/Header');
const logger = require('../../conf/logger')(__filename);
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
    this._routeName = value =>
      element(by.css(`[e2e='route-routeName-${value}']`));
    this._filePattern = value =>
      element(by.css(`[e2e='route-filePattern-${value}']`));
    this._sourceLocation = value =>
      element(by.css(`[e2e='route-sourceLocation-${value}']`));
    this._destLocation = value =>
      element(by.css(`[e2e='route-destinationLocation-${value}']`));
    this._description = value =>
      element(by.css(`[e2e='route-description-${value}']`));
    this._createdBy = element(
      by.xpath(`//*[contains(@e2e,'route-createdBy')]`)
    );
    this._scheduleInfo = element(
      by.xpath(`//*[contains(@e2e,'route-schedulerExpression-')]`)
    );
    this._nextFireTime = element(
      by.xpath(`//*[contains(@e2e,'route-nextFireTime-')]`)
    );
    this._editRoute = element(by.css(`[e2e='edit-route-btn']`));

    //Jobs page
    this._channelName = value =>
      element(by.css(`[e2e='job-channelName-${value}']`));
    this._jobRouteName = value =>
      element(by.css(`[e2e='job-routeName-${value}']`));
    this._jobFilePattern = value =>
      element(by.css(`[e2e='job-filePattern-${value}']`));
    this._jobIdByRouteName = value =>
      element(
        by.xpath(
          `(//*[@e2e='job-routeName-${value}'])[1]/preceding::div[contains(@e2e,'job-jobId')]/a`
        )
      );

    //Job details
    this._jobLogFilePattern = value =>
      element(by.css(`[e2e='job-log-filePattern-${value}']`));
    this._jobLogFileName = value =>
      element(by.css(`[e2e='job-log-fileName-${value}']`));
    this._jobLogProcessState = value =>
      element(by.css(`[e2e='job-log-bisProcessState-${value}']`));
    this._jobLogFileStatus = value =>
      element(by.css(`[e2e='job-log-mflFileStatus-${value}']`));

    this._backBtn = element(by.css(`[class='mat-icon-button']`));
    this._recFileName = name =>
      element(by.xpath(`//*[contains(@e2e,'job-log-recdFileName-${name}')]`));
  }

  clickOnJobIdByRouteName(name) {
    commonFunctions.clickOnElement(this._jobIdByRouteName(name));
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

  activateChannel(name) {
    commonFunctions.clickOnElement(this._channelActivateDeactivate(name, 0));
    commonFunctions.waitFor.elementToBeVisible(
      this._channelActivateDeactivate(name, 1)
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

  verifyRouteDetails(routeInfo, apipull = false) {
    commonFunctions.waitFor.elementToBeVisible(
      this._routeName(routeInfo.routeName)
    );
    if (!apipull) {
      commonFunctions.waitFor.elementToBeVisible(
        this._sourceLocation(routeInfo.source)
      );
      commonFunctions.waitFor.elementToBeVisible(
        this._filePattern(routeInfo.filePattern)
      );
    }
    commonFunctions.validateHasText(this._createdBy);
    commonFunctions.validateHasText(this._scheduleInfo);
    commonFunctions.validateHasText(this._nextFireTime);
    commonFunctions.waitFor.elementToBeVisible(
      this._destLocation(routeInfo.destination)
    );
    commonFunctions.waitFor.elementToBeVisible(
      this._description(routeInfo.desc)
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

  clickOnBackButton() {
    commonFunctions.clickOnElement(this._backBtn);
  }

  verifyRouteScheduleInformation(channelName, routeInfo) {
    let _self = this;
    let attempts = 16;
    let skip = true; // this is added to give time to process the file
    (function process(index) {
      if (index >= attempts) {
        return;
      }
      _self.clickOnCreatedChannelName(channelName);
      _self.clickOnRouteAction(routeInfo.routeName);
      _self.clickOnViewRouteLogs();
      browser.sleep(2000);

      element(
        _self
          ._jobFilePattern(routeInfo.filePattern)
          .isPresent()
          .then(present => {
            if (present) {
              if (skip) {
                // skipping first occurance to give time for scheduler to process the file
                // Added as part of SIP-9320
                _self.clickOnBackButton();
                logger.info(
                  `Element found but skipping it as this is first occurrence`
                );
                logger.info(`waiting for 20 seconds and check again`);
                browser.sleep(20000);
                skip = false; // set the skip false
                process(index + 1);
              } else {
                logger.info(
                  `Element found... and this is second occurrence. Validating the data`
                );
                _self.clickOnJobIdByRouteName(routeInfo.routeName);
                _self.scheduleVerification(routeInfo);
                // go to channel management
                _self.clickOnBackButton();
                _self.clickOnBackButton();
              }
            } else {
              // go to channel management
              _self.clickOnBackButton();
              logger.info(`Element not present Attempt:${index} done`);
              logger.info(`waiting for 20 seconds and check again`);
              browser.sleep(20000);
              process(index + 1);
            }
          })
      );
    })(1);
  }

  goToSubCat(cat, subCat) {
    const header = new Header();
    header.openCategoryMenu();
    header.selectCategory(cat);
    header.selectSubCategory(subCat);
  }

  scheduleVerification(routeInfo) {
    expect(
      this._jobLogFilePattern(routeInfo.filePattern).isDisplayed()
    ).toBeTruthy();
    expect(
      this._jobLogFileName(
        `${routeInfo.source}/${routeInfo.fileName}`
      ).isDisplayed()
    ).toBeTruthy();
    expect(this._jobLogFileStatus(`SUCCESS`).isDisplayed()).toBeTruthy();
    expect(
      this._jobLogProcessState(`DATA_RECEIVED`).isDisplayed()
    ).toBeTruthy();
  }

  /**
   *
   * @param {Object} channelInfo
   * contains all the property which are displayed in current displayed channel
   */
  verifyCurrentDisplayedApiChannel(channelInfo) {
    commonFunctions.waitFor.elementToBeVisible(this._channelNameDetail);
    commonFunctions.validateText(
      this._channelNameDetail,
      channelInfo.channelName
    );
    commonFunctions.validateText(
      this._channelAttributes('host-name'),
      channelInfo.hostName
    );
    commonFunctions.validateText(
      this._channelAttributes('port-number'),
      channelInfo.port
    );
    commonFunctions.validateText(
      this._channelAttributes('created-by'),
      channelInfo.created
    );

    commonFunctions.validateText(
      this._channelAttributes('description'),
      channelInfo.desc
    );
  }

  clickOnEditRoute() {
    commonFunctions.clickOnElement(this._editRoute);
  }

  verifyApiRouteScheduleInformation(channelName, routeInfo) {
    let _self = this;
    let attempts = 16;
    let skip = true;
    (function process(index) {
      if (index >= attempts) {
        return;
      }
      _self.clickOnCreatedChannelName(channelName);
      _self.clickOnRouteAction(routeInfo.routeName);
      _self.clickOnViewRouteLogs();
      browser.sleep(2000);

      element(
        _self
          ._jobRouteName(routeInfo.routeName)
          .isPresent()
          .then(present => {
            if (present) {
              if (skip) {
                // skipping first occurance to give time for scheduler to process the file
                // Added as part of SIP-9320
                _self.clickOnBackButton();
                logger.info(
                  `Element found but skipping it as this is first occurrence`
                );
                logger.info(`waiting for 20 seconds and check again`);
                browser.sleep(20000);
                skip = false; // set the skip false
                process(index + 1);
              } else {
                logger.info(
                  `Element found... and this is second occurrence. Validating the data`
                );
                _self.clickOnJobIdByRouteName(routeInfo.routeName);
                _self.scheduleAPiLogVerification(routeInfo);
                // go to channel management
                _self.clickOnBackButton();
                _self.clickOnBackButton();
              }
            } else {
              // go to channel management
              _self.clickOnBackButton();
              logger.info(`Element not present Attempt:${index} done`);
              logger.info(`waiting for 20 seconds and check again`);
              browser.sleep(20000);
              process(index + 1);
            }
          })
      );
    })(1);
  }

  scheduleAPiLogVerification(routeInfo) {
    const recFileName = routeInfo.routeName.replace(/ /g, '_').toLowerCase();
    expect(this._recFileName(recFileName).isDisplayed()).toBeTruthy();
    expect(this._jobLogFileStatus(`SUCCESS`).isDisplayed()).toBeTruthy();
    expect(
      this._jobLogProcessState(`DATA_RECEIVED`).isDisplayed()
    ).toBeTruthy();
  }
}

module.exports = DataSourcesPage;
