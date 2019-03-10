var testDataReader = require('../../testdata/testDataReader.js');
var appRoot = require('app-root-path');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../protractor.conf');
const users = require('../../../v2/helpers/data-generation/users');
const Constants = require('../../../v2/helpers/Constants');
const Utils = require('../../../v2/helpers/Utils');
const APICommonHelpers = require('../../../v2/helpers/api/APICommonHelpers');
const FtpHelper = require('../../../v2/helpers/ftp/FtpHelper');

const commonFunctions = require('../../../v2/pages/utils/commonFunctions');
const DataSourcesPage = require('../../../v2/pages/workbench/DataSourcesPage');
const ChannelActions = require('../../../v2/pages/workbench/ChannelActions');
const RouteActions = require('../../../v2/pages/workbench/RouteActions');
const Header = require('../../../v2/pages/components/Header');
const LoginPage = require('../../../v2/pages/LoginPage');
const logger = require('../../../v2/conf/logger')(__filename);
var fs = require('fs');

describe('Workbench tests: updateAndDelete.test.js', () => {
  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['BIS_CHANNEL']['updateAndDelete'],
    (data, description) => {
      it(
        data.description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'BIS_CHANNEL',
            dp: 'updateAndDelete'
          }),
        () => {
          logger.warn(`Running testCase with id: ${data.id}`);
          logger.warn(`Data: ` + JSON.stringify(data));
          const channelName = `${data.channelName}${Utils.randomId()}`;
          const channelDescription = `${
            data.channelName
          } description created at ${Utils.randomId()}`;

          let channelInfo = {
            sourceType: data.sourceType,
            channelName,
            access: data.accessType,
            sftpHost: Constants.SFTP_DETAILS.sftpHost,
            sftpPort: Constants.SFTP_DETAILS.sftpPort,
            sftpUser: Constants.SFTP_DETAILS.sftpUser,
            sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
            desc: channelDescription,
            created: users.admin.firstName + ' ' + users.admin.lastName,
            status: data.status
          };

          const loginPage = new LoginPage();
          loginPage.loginAs(data.user);

          const header = new Header();
          header.clickOnLauncher();
          header.clickOnWorkBench();

          const dataSourcesPage = new DataSourcesPage();
          dataSourcesPage.clickOnAddChannelButton();

          const channelActions = new ChannelActions();
          // Create new channel
          channelActions.createNewChannel(channelInfo);
          dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
          // Update created channel information
          let updatedChannelInfo = {
            sourceType: data.sourceType,
            channelName: channelName + 'up',
            access: data.accessType,
            sftpHost: Constants.SFTP_DETAILS.sftpHost,
            sftpPort: Constants.SFTP_DETAILS.sftpPort,
            sftpUser: Constants.SFTP_DETAILS.sftpUser,
            sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
            desc: channelDescription + 'up',
            created: users.admin.firstName + ' ' + users.admin.lastName,
            status: data.status
          };
          dataSourcesPage.clickOnEditChannel();
          channelActions.fillChannelName(updatedChannelInfo.channelName);
          channelActions.selectAccessType(updatedChannelInfo.access);
          channelActions.enterHostName(updatedChannelInfo.sftpHost);
          channelActions.fillUserName(updatedChannelInfo.sftpUser);
          channelActions.fillPortNumber(updatedChannelInfo.sftpPort);
          channelActions.fillPassword(updatedChannelInfo.sftpPwd);
          channelActions.fillDescription(updatedChannelInfo.desc);
          channelActions.clickOnTestConnectivity();
          channelActions.verifyTestConnectivityLogs(
            data.testConnectivityMessage
          );
          channelActions.closeTestConnectivity();
          channelActions.clickOnUpdateChannel();
          // Verifications
          dataSourcesPage.verifyChannelDetailsInListView(
            updatedChannelInfo.channelName,
            updatedChannelInfo.sftpHost,
            updatedChannelInfo.status
          );
          dataSourcesPage.clickOnCreatedChannelName(
            updatedChannelInfo.channelName
          );
          dataSourcesPage.verifyCurrentDisplayedChannel(updatedChannelInfo);
          dataSourcesPage.clickOnDeleteChannel();
          dataSourcesPage.clickOnConfirmYesButton();
          dataSourcesPage.verifyChannelNotDeleted();
        }
      );
    }
  );
});
