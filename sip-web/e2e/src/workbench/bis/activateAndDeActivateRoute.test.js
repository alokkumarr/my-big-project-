const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../protractor.conf');
const users = require('../../../v2/helpers/data-generation/users');
const Constants = require('../../../v2/helpers/Constants');
const Utils = require('../../../v2/helpers/Utils');
const APICommonHelpers = require('../../../v2/helpers/api/APICommonHelpers');
const SshUtility = require('../../../v2/helpers/ftp/SshUtility');

const commonFunctions = require('../../../v2/pages/utils/commonFunctions');
const DataSourcesPage = require('../../../v2/pages/workbench/DataSourcesPage');
const ChannelActions = require('../../../v2/pages/workbench/ChannelActions');
const RouteActions = require('../../../v2/pages/workbench/RouteActions');
const Header = require('../../../v2/pages/components/Header');
const LoginPage = require('../../../v2/pages/LoginPage');
const logger = require('../../../v2/conf/logger')(__filename);

describe('Workbench tests: activateAndDeActivateRoute.test.js', () => {
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
    testDataReader.testData['BIS']['activateDeactivateRoute'],
    (data, description) => {
      it(
        data.description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'BIS',
            dp: 'activateDeactivateRoute'
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

          const num = Math.floor(Math.random() * 10 + 1);
          const time = new Date().getTime();
          const content = `This is content for file ${time} and ${num}`;
          const fileName = `file${time}.txt${num}`;
          const source = `source${time}`;
          const destination = `/dest${time}`;
          const host = APICommonHelpers.getHost(browser.baseUrl);

          new SshUtility(
            host,
            8022,
            'root',
            'root'
          ).createDirectoryAndDummyFile(source, content, fileName);

          let routeInfo = {
            routeName,
            source: `/root/${source}`,
            filePattern: fileName,
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
          // Add route
          dataSourcesPage.clickOnAddRoute();

          const routeActions = new RouteActions();
          routeActions.createRoute(routeInfo);
          dataSourcesPage.verifyRouteDetails(routeInfo);

          if (data.type === 'Activate') {
            dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
            dataSourcesPage.clickOnActivateDeActiveRoute();
            browser.sleep(500); // Added as sometime data was not loaded quickly
            dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
            dataSourcesPage.clickOnActivateDeActiveRoute();
            browser.sleep(500); // Added as sometime data was not loaded quickly
          } else if (data.type === 'Deactivate') {
            dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
            dataSourcesPage.clickOnActivateDeActiveRoute();
            browser.sleep(500); // Added as sometime data was not loaded quickly
          }
          dataSourcesPage.clickOnRouteAction(routeInfo.routeName);
          if (data.type === 'Activate') {
            dataSourcesPage.verifyRouteStatus('Deactivate');
          } else if (data.type === 'Deactivate') {
            dataSourcesPage.verifyRouteStatus('Activate');
          }
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
