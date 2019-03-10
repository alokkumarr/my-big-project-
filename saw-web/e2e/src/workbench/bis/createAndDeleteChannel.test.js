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

describe('Workbench tests: createAndDelete.test.js', () => {
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

  // using(
  //   testDataReader.testData['BIS_CHANNEL']['createAndDelete'],
  //   (data, description) => {
  //     it(
  //       data.description +
  //         ' testDataMetaInfo: ' +
  //         JSON.stringify({
  //           test: description,
  //           feature: 'BIS_CHANNEL',
  //           dp: 'createAndDelete'
  //         }),
  //       () => {
  //         logger.warn(`Running testCase with id: ${data.id}`);
  //         logger.warn(`Data: ` + JSON.stringify(data));
  //         const channelName = `${data.channelName}${Utils.randomId()}`;
  //         const channelDescription = `${
  //           data.channelName
  //         } description created at ${Utils.randomId()}`;
  //
  //         let channelInfo = {
  //           sourceType: data.sourceType,
  //           channelName,
  //           access: data.accessType,
  //           sftpHost: Constants.SFTP_DETAILS.sftpHost,
  //           sftpPort: Constants.SFTP_DETAILS.sftpPort,
  //           sftpUser: Constants.SFTP_DETAILS.sftpUser,
  //           sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
  //           desc: channelDescription,
  //           created: users.admin.firstName + ' ' + users.admin.lastName,
  //           status: data.status
  //         };
  //
  //         const loginPage = new LoginPage();
  //         loginPage.loginAs(data.user);
  //
  //         const header = new Header();
  //         header.clickOnLauncher();
  //         header.clickOnWorkBench();
  //
  //         const dataSourcesPage = new DataSourcesPage();
  //         dataSourcesPage.clickOnAddChannelButton();
  //
  //         const channelActions = new ChannelActions();
  //         channelActions.clickOnChannelType(data.sourceType);
  //         channelActions.clickOnChannelNextButton();
  //         channelActions.fillChannelName(channelInfo.channelName);
  //         channelActions.selectAccessType(channelInfo.access);
  //         channelActions.enterHostName(channelInfo.sftpHost);
  //         channelActions.fillUserName(channelInfo.sftpUser);
  //         channelActions.fillPortNumber(channelInfo.sftpPort);
  //         channelActions.fillPassword(channelInfo.sftpPwd);
  //         channelActions.fillDescription(channelInfo.desc);
  //         channelActions.clickOnTestConnectivity();
  //         channelActions.verifyTestConnectivityLogs(
  //           data.testConnectivityMessage
  //         );
  //         channelActions.closeTestConnectivity();
  //         channelActions.clickOnCreateButton();
  //         // Verifications
  //         dataSourcesPage.verifyChannelDetailsInListView(
  //           channelInfo.channelName,
  //           channelInfo.sftpHost,
  //           channelInfo.status
  //         );
  //         dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
  //         dataSourcesPage.verifyCurrentDisplayedChannel(channelInfo);
  //         dataSourcesPage.clickOnDeleteChannel();
  //         dataSourcesPage.clickOnConfirmYesButton();
  //         dataSourcesPage.verifyChannelNotDeleted();
  //       }
  //     );
  //   }
  // );
  //
  // using(
  //   testDataReader.testData['BIS_CHANNEL']['updateAndDelete'],
  //   (data, description) => {
  //     it(
  //       data.description +
  //         ' testDataMetaInfo: ' +
  //         JSON.stringify({
  //           test: description,
  //           feature: 'BIS_CHANNEL',
  //           dp: 'updateAndDelete'
  //         }),
  //       () => {
  //         logger.warn(`Running testCase with id: ${data.id}`);
  //         logger.warn(`Data: ` + JSON.stringify(data));
  //         const channelName = `${data.channelName}${Utils.randomId()}`;
  //         const channelDescription = `${
  //           data.channelName
  //         } description created at ${Utils.randomId()}`;
  //
  //         let channelInfo = {
  //           sourceType: data.sourceType,
  //           channelName,
  //           access: data.accessType,
  //           sftpHost: Constants.SFTP_DETAILS.sftpHost,
  //           sftpPort: Constants.SFTP_DETAILS.sftpPort,
  //           sftpUser: Constants.SFTP_DETAILS.sftpUser,
  //           sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
  //           desc: channelDescription,
  //           created: users.admin.firstName + ' ' + users.admin.lastName,
  //           status: data.status
  //         };
  //
  //         const loginPage = new LoginPage();
  //         loginPage.loginAs(data.user);
  //
  //         const header = new Header();
  //         header.clickOnLauncher();
  //         header.clickOnWorkBench();
  //
  //         const dataSourcesPage = new DataSourcesPage();
  //         dataSourcesPage.clickOnAddChannelButton();
  //
  //         const channelActions = new ChannelActions();
  //         // Create new channel
  //         channelActions.createNewChannel(channelInfo);
  //         dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
  //         // Update created channel information
  //         let updatedChannelInfo = {
  //           sourceType: data.sourceType,
  //           channelName: channelName + 'up',
  //           access: data.accessType,
  //           sftpHost: Constants.SFTP_DETAILS.sftpHost,
  //           sftpPort: Constants.SFTP_DETAILS.sftpPort,
  //           sftpUser: Constants.SFTP_DETAILS.sftpUser,
  //           sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
  //           desc: channelDescription + 'up',
  //           created: users.admin.firstName + ' ' + users.admin.lastName,
  //           status: data.status
  //         };
  //         dataSourcesPage.clickOnEditChannel();
  //         channelActions.fillChannelName(updatedChannelInfo.channelName);
  //         channelActions.selectAccessType(updatedChannelInfo.access);
  //         channelActions.enterHostName(updatedChannelInfo.sftpHost);
  //         channelActions.fillUserName(updatedChannelInfo.sftpUser);
  //         channelActions.fillPortNumber(updatedChannelInfo.sftpPort);
  //         channelActions.fillPassword(updatedChannelInfo.sftpPwd);
  //         channelActions.fillDescription(updatedChannelInfo.desc);
  //         channelActions.clickOnTestConnectivity();
  //         channelActions.verifyTestConnectivityLogs(
  //           data.testConnectivityMessage
  //         );
  //         channelActions.closeTestConnectivity();
  //         channelActions.clickOnUpdateChannel();
  //         // Verifications
  //         dataSourcesPage.verifyChannelDetailsInListView(
  //           updatedChannelInfo.channelName,
  //           updatedChannelInfo.sftpHost,
  //           updatedChannelInfo.status
  //         );
  //         dataSourcesPage.clickOnCreatedChannelName(
  //           updatedChannelInfo.channelName
  //         );
  //         dataSourcesPage.verifyCurrentDisplayedChannel(updatedChannelInfo);
  //         dataSourcesPage.clickOnDeleteChannel();
  //         dataSourcesPage.clickOnConfirmYesButton();
  //         dataSourcesPage.verifyChannelNotDeleted();
  //       }
  //     );
  //   }
  // );

  // using(
  //   testDataReader.testData['BIS_CHANNEL']['deActivateChannel'],
  //   (data, description) => {
  //     it(
  //       data.description +
  //         ' testDataMetaInfo: ' +
  //         JSON.stringify({
  //           test: description,
  //           feature: 'BIS_CHANNEL',
  //           dp: 'deActivateChannel'
  //         }),
  //       () => {
  //         logger.warn(`Running testCase with id: ${data.id}`);
  //         logger.warn(`Data: ` + JSON.stringify(data));
  //         const channelName = `${data.channelName}${Utils.randomId()}`;
  //         const channelDescription = `${
  //           data.channelName
  //         } description created at ${Utils.randomId()}`;
  //
  //         let channelInfo = {
  //           sourceType: data.sourceType,
  //           channelName,
  //           access: data.accessType,
  //           sftpHost: Constants.SFTP_DETAILS.sftpHost,
  //           sftpPort: Constants.SFTP_DETAILS.sftpPort,
  //           sftpUser: Constants.SFTP_DETAILS.sftpUser,
  //           sftpPwd: Constants.SFTP_DETAILS.sftpPassword,
  //           desc: channelDescription,
  //           created: users.admin.firstName + ' ' + users.admin.lastName,
  //           status: data.status
  //         };
  //
  //         const loginPage = new LoginPage();
  //         loginPage.loginAs(data.user);
  //
  //         const header = new Header();
  //         header.clickOnLauncher();
  //         header.clickOnWorkBench();
  //
  //         const dataSourcesPage = new DataSourcesPage();
  //         dataSourcesPage.clickOnAddChannelButton();
  //
  //         const channelActions = new ChannelActions();
  //         // Create new channel
  //         channelActions.createNewChannel(channelInfo);
  //         dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
  //         dataSourcesPage.deActivateChannel(channelInfo.channelName);
  //         dataSourcesPage.clickOnDeleteChannel();
  //         dataSourcesPage.clickOnConfirmYesButton();
  //         dataSourcesPage.verifyChannelNotDeleted();
  //       }
  //     );
  //   }
  // );

  using(
    testDataReader.testData['BIS_CHANNEL']['createAndDeleteRoute'],
    (data, description) => {
      it(
        data.description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'BIS_CHANNEL',
            dp: 'createAndDeleteRoute'
          }),
        () => {
          logger.warn(`Running testCase with id: ${data.id}`);

          const uId = Utils.randomId();
          const channelName = `${data.channelName}${uId}`;
          const channelDescription = `${
            data.channelName
          } description created at ${uId}`;

          const routeName = `${data.routeName}${uId}`;
          const desc = `${data.routeName} description created at ${uId}`;

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

          let fileName = `fileName${uId}.txt`;
          let batchSize = '' + Math.floor(Math.random() * 10 + 1);
          let destination = `/dest${uId}`;
          new FtpHelper().copyFile(
            APICommonHelpers.getHost(browser.baseUrl),
            fileName
          );

          let routeInfo = {
            routeName,
            source: '/home/centos',
            filePattern: fileName,
            destination,
            batchSize,
            desc,
            created: users.admin.firstName + ' ' + users.admin.lastName
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
          // Add route
          dataSourcesPage.clickOnAddRoute();

          const routeActions = new RouteActions();
          routeActions.fillRouteName(routeInfo.routeName);
          routeActions.fillRouteSource(routeInfo.source);
          routeActions.fillRouteFilePattern(routeInfo.filePattern);
          routeActions.fillRouteDestination(routeInfo.destination);
          routeActions.fillRouteBatchSize(routeInfo.batchSize);
          routeActions.fillRouteDescription(routeInfo.desc);
          routeActions.clickOnRouteNextBtn();
          routeActions.clickOnScheduleTab('Hourly');
          routeActions.clickOnFrequency('Hour', 0);
          routeActions.clickOnFrequency('Minute', 2);

          routeActions.clickOnTestConnectivity();
          routeActions.verifyTestConnectivityLogs(data.testConnectivityMessage);
          routeActions.closeTestConnectivity();

          routeActions.setScheduleStartDate();
          const currentTime = new Date().getTime();
          routeActions.clickOnCreateRouteBtn();
          dataSourcesPage.verifyRouteDetails(routeInfo);
          dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
          dataSourcesPage.clickOnDeleteRoute();
          dataSourcesPage.clickOnConfirmYesButton();
          dataSourcesPage.verifyRouteDeleted(routeInfo.routeName);
          dataSourcesPage.clickOnDeleteChannel();
          dataSourcesPage.clickOnConfirmYesButton();
          dataSourcesPage.verifyChannelNotDeleted();
        }
      );
    }
  );
});
