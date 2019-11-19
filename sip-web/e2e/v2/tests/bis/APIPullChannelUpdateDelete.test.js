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

describe('BIS API PULL tests: APIPullChannelUpdateDelete.test.js', () => {
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
    testDataReader.testData['BIS_APIPULL']['updateAndDeleteChannel']
      ? testDataReader.testData['BIS_APIPULL']['updateAndDeleteChannel']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${id}`);
        logger.warn(`Data: ` + JSON.stringify(data));

        data.channelInfo.channelName = `${data.channelInfo.channelName}${Utils.randomId()}`;

        data.channelInfo.desc = `${data.channelInfo.channelName} description created at ${Utils.randomId()}`;

        data.channelInfo.created = users.admin.firstName + ' ' + users.admin.lastName;

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user);

        const header = new Header();
        header.clickOnLauncher();
        header.clickOnWorkBench();

        const dataSourcesPage = new DataSourcesPage();
        dataSourcesPage.clickOnAddChannelButton();

        const channelActions = new ChannelActions();
        channelActions.createNewApiChannel(data.channelInfo);
        let updatedChannelInfo = {
          channelName: `${data.channelInfo.channelName}-up`,
          hostName: `${data.channelInfo.hostName}`,
          port: `${data.channelInfo.port}`,
          method: `${data.channelInfo.method}`,
          endPoint: `${data.channelInfo.endPoint}`,
          headers: data.channelInfo.headers,
          queryParams: data.channelInfo.queryParams,
          desc: `${data.channelInfo.desc}-up`,
          testConnectivityMessage: `${data.channelInfo.testConnectivityMessage}`,
          status: `${data.channelInfo.status}`,
          created: `${data.channelInfo.created}`
        };
        //update
        dataSourcesPage.clickOnCreatedChannelName(data.channelInfo.channelName);
        dataSourcesPage.clickOnEditChannel();
        channelActions.fillChannelName(updatedChannelInfo.channelName);
        channelActions.fillHostName(updatedChannelInfo.hostName);
        if (updatedChannelInfo.port) channelActions.fillPortNumber(updatedChannelInfo.port);
        channelActions.selectMethodType(updatedChannelInfo.method);
        channelActions.fillEndPoint(updatedChannelInfo.endPoint);
        if (updatedChannelInfo.method === 'POST')
          channelActions.fillRequestBody(JSON.stringify(updatedChannelInfo.body));
        if (updatedChannelInfo.headers) {
          channelActions.clearHeader();
          channelActions.addHeaders(updatedChannelInfo.headers);
        }
        if (updatedChannelInfo.queryParams) {
          channelActions.clearQueryParams();
          channelActions.addQueryParams(updatedChannelInfo.queryParams);
        }
        channelActions.fillDescription(updatedChannelInfo.desc);
        channelActions.clickOnTestConnectivity();
        channelActions.verifyTestConnectivityLogs(updatedChannelInfo.testConnectivityMessage);
        channelActions.closeTestConnectivity();
        channelActions.clickOnUpdateChannel();
        // Verifications
        dataSourcesPage.verifyChannelDetailsInListView(
          updatedChannelInfo.channelName,
          updatedChannelInfo.hostName,
          updatedChannelInfo.status
        );
        dataSourcesPage.clickOnCreatedChannelName(updatedChannelInfo.channelName);
        dataSourcesPage.verifyCurrentDisplayedApiChannel(updatedChannelInfo);
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS_APIPULL',
        dataProvider: 'updateAndDeleteChannel'
      };
    }
  );
});
