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
