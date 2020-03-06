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
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

describe('BIS API PULL tests: APIPullChannelUpdateDelete.test.js', () => {
  let token;

  beforeAll(function() {
    logger.info(`started executing BIS API PULL tests`);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
    const host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(
      host,
      users.masterAdmin.loginId,
      users.masterAdmin.password
    );
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

        data.channelInfo.channelName = `${
          data.channelInfo.channelName
        }${Utils.randomId()}`;

        data.channelInfo.desc = `${
          data.channelInfo.channelName
        } description created at ${Utils.randomId()}`;

        data.channelInfo.created =
          users.admin.firstName + ' ' + users.admin.lastName;

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user);

        const header = new Header();
        header.clickOnLauncher();
        header.clickOnWorkBench();

        const dataSourcesPage = new DataSourcesPage();
        dataSourcesPage.clickOnAddChannelButton();

        const channelActions = new ChannelActions();

        channelActions.clickOnChannelType(data.channelInfo.sourceType);
        channelActions.clickOnChannelNextButton();

        channelActions.fillApiChannleInfo(data.channelInfo, false, token);
        channelActions.testAndVerifyTestConnectivity(
          data.channelInfo.testConnectivityMessage
        );
        channelActions.clickOnCreateButton();

        let updatedChannelInfo = Object.assign({}, data.channelInfo);
        updatedChannelInfo.channelName = `${updatedChannelInfo.channelName}-up`;
        updatedChannelInfo.desc = `${updatedChannelInfo.desc}-up`;
        //update
        dataSourcesPage.clickOnCreatedChannelName(data.channelInfo.channelName);
        dataSourcesPage.clickOnEditChannel();
        channelActions.fillApiChannleInfo(updatedChannelInfo, true, token);
        channelActions.testAndVerifyTestConnectivity(
          updatedChannelInfo.testConnectivityMessage
        );
        channelActions.clickOnUpdateChannel();
        // Verifications
        dataSourcesPage.verifyChannelDetailsInListView(
          updatedChannelInfo.channelName,
          updatedChannelInfo.hostName,
          updatedChannelInfo.status
        );
        dataSourcesPage.clickOnCreatedChannelName(
          updatedChannelInfo.channelName
        );
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
