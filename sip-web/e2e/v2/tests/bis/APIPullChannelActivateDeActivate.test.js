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

describe('BIS API PULL tests: APIPullChannelDeActivate.test.js', () => {
  beforeAll(function() {
    logger.info(`started executing APIPullChannelDeActivate.test.js tests`);
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
    logger.info(`completed executing APIPullChannelDeActivate.test.js`);
  });

  using(
    testDataReader.testData['BIS_APIPULL']['ActivateDeactivateAndDeleteChannel']
      ? testDataReader.testData['BIS_APIPULL']['ActivateDeactivateAndDeleteChannel']
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

        channelActions.clickOnChannelType(data.channelInfo.sourceType);
        channelActions.clickOnChannelNextButton();

        channelActions.fillApiChannleInfo(data.channelInfo);
        channelActions.testAndVerifyTestConnectivity(data.channelInfo.testConnectivityMessage);
        channelActions.clickOnCreateButton();
        dataSourcesPage.clickOnCreatedChannelName(data.channelInfo.channelName);
        if (data.channelInfo.status == 1) {
          dataSourcesPage.deActivateChannel(data.channelInfo.channelName);
          dataSourcesPage.activateChannel(data.channelInfo.channelName);
        } else {
          dataSourcesPage.deActivateChannel(data.channelInfo.channelName);
        }

        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS_APIPULL',
        dataProvider: 'ActivateDeactivateAndDeleteChannel'
      };
    }
  );
});
