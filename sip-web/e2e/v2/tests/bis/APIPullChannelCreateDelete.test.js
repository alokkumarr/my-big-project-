const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const users = require('../../helpers/data-generation/users');
const Constants = require('../../helpers/Constants');
const Utils = require('../../helpers/Utils');

const commonFunctions = require('../../pages/utils/commonFunctions');
const DataSourcesPage = require('../../pages/workbench/DataSourcesPage');
const ChannelActions = require('../../pages/workbench/ChannelActions');
const LoginPage = require('../../pages/LoginPage');
const logger = require('../../conf/logger')(__filename);
const Header = require('../../pages/components/Header');

describe('BIS API PULL tests: APIPullChannelCreateDelete.test.js', () => {
  beforeAll(function() {
    logger.info(`started executing BIS API PULL tests`);
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

  afterAll(() => {
    logger.info(`completed executing BIS API PULL tests`);
  });

  using(
    testDataReader.testData['BIS_APIPULL']['createAndDeleteChannel']
      ? testDataReader.testData['BIS_APIPULL']['createAndDeleteChannel']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${id}`);
        logger.warn(`Data: ` + JSON.stringify(data));
        const channelName = `${
          data.channelInfo.channelName
        }${Utils.randomId()}`;
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
        channelActions.clickOnChannelType(data.sourceType);
        channelActions.clickOnChannelNextButton();
        channelActions.fillChannelName(channelInfo.channelName);
        channelActions.fillHostName(data.channelInfo.hostName);
        if (data.channelInfo.port)
          channelActions.fillPortNumber(data.channelInfo.port);
        channelActions.selectMethodType(data.channelInfo.method);
        if (data.channelInfo.headers)
          channelActions.addHeaders(data.channelInfo.headers);
        if (data.channelInfo.queryParams)
          channelActions.addQueryParams(data.channelInfo.queryParams);
        channelActions.fillDescription(channelInfo.desc);
        channelActions.clickOnTestConnectivity();
        channelActions.verifyTestConnectivityLogs(data.testConnectivityMessage);
        channelActions.closeTestConnectivity();
        channelActions.clickOnCreateButton();
        // // Verifications
        // dataSourcesPage.verifyChannelDetailsInListView(
        //   channelInfo.channelName,
        //   channelInfo.sftpHost,
        //   channelInfo.status
        // );
        // dataSourcesPage.clickOnCreatedChannelName(channelInfo.channelName);
        // dataSourcesPage.verifyCurrentDisplayedChannel(channelInfo);
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS_APIPULL',
        dataProvider: 'createAndDeleteChannel'
      };
    }
  );
});
