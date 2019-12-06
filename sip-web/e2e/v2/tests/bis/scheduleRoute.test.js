const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const users = require('../../helpers/data-generation/users');
const Constants = require('../../helpers/Constants');
const Utils = require('../../helpers/Utils');
const APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const SshUtility = require('../../helpers/ftp/SshUtility');

const commonFunctions = require('../../pages/utils/commonFunctions');
const DataSourcesPage = require('../../pages/workbench/DataSourcesPage');
const ChannelActions = require('../../pages/workbench/ChannelActions');
const RouteActions = require('../../pages/workbench/RouteActions');
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const logger = require('../../conf/logger')(__filename);

describe('BIS tests: scheduleRoute.test.js', () => {
  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
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
    testDataReader.testData['BIS']['routeSchedule']
      ? testDataReader.testData['BIS']['routeSchedule']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${data.id}`);

        const uId = Utils.randomId();

        const channelName = `${data.channelName}${uId}`;
        const channelDescription = `${data.channelName} description created at ${uId}`;

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

        const num = Math.floor(Math.random() * 10 + 1);
        const time = new Date().getTime();
        const content = `This is content for file ${time} and ${num}`;
        const fileName = `file${time}.txt${num}`;
        const source = `source${time}`;
        const destination = `/dest${time}`;
        const host = APICommonHelpers.getHost(browser.baseUrl);

        new SshUtility(host, 8022, 'root', 'root').createDirectoryAndDummyFile(
          source,
          content,
          fileName
        );

        let routeInfo = {
          routeName,
          source: `/root/${source}`,
          filePattern: fileName,
          fileName,
          destination,
          batchSize: num,
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
        dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
        // Add route
        dataSourcesPage.clickOnAddRoute();

        const routeActions = new RouteActions();
        routeActions.createRoute(routeInfo);
        dataSourcesPage.verifyRouteDetails(routeInfo);
        // Verify schedule logs
        dataSourcesPage.verifyRouteScheduleInformation(
          channelInfo.channelName,
          routeInfo
        );
        dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
        dataSourcesPage.clickOnDeleteRoute();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyRouteDeleted(routeInfo.routeName);
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS',
        dataProvider: 'routeSchedule'
      };
    }
  );
});
