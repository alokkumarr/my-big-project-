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
const RouteActions = require('../../pages/workbench/RouteActions');

describe('BIS API PULL tests: APIPullRouteCreateDelete.test.js', () => {
  beforeAll(function() {
    logger.info(`started executing BIS API PULL tests APIPullRouteCreateDelete.test.js`);
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
    logger.info(`completed executing BIS API PULL tests APIPullRouteCreateDelete.test.js`);
  });

  using(
    testDataReader.testData['BIS_APIPULL']['createAndDeleteRoute']
      ? testDataReader.testData['BIS_APIPULL']['createAndDeleteRoute']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${id}`);
        logger.warn(`Data: ` + JSON.stringify(data));
        const time = new Date().getTime();
        data.channelInfo.channelName = `${data.channelInfo.channelName}${Utils.randomId()}`;

        data.channelInfo.desc = `${data.channelInfo.channelName} description created at ${Utils.randomId()}`;

        data.channelInfo.created = users.admin.firstName + ' ' + users.admin.lastName;
        data.routeInfo.routeName = `${data.routeInfo.routeName}${Utils.randomId()}`;
        data.routeInfo.desc = `${data.routeInfo.routeName} description ${Utils.randomId()}`;
        data.routeInfo.destination = `/dest${time}`;

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
        dataSourcesPage.clickOnAddRoute();
        // create route
        const routeActions = new RouteActions();
        routeActions.fillApiRouteName(data.routeInfo.routeName);
        routeActions.fillApiRouteDestination(data.routeInfo.destination);
        channelActions.selectMethodType(data.routeInfo.method);
        channelActions.fillEndPoint(data.routeInfo.endPoint);
        if (data.routeInfo.method === 'POST') channelActions.fillRequestBody(JSON.stringify(data.routeInfo.body));
        if (data.routeInfo.headers) {
          channelActions.addHeaders(data.routeInfo.headers);
        }
        if (data.routeInfo.queryParams) {
          channelActions.addQueryParams(data.routeInfo.queryParams);
        }
        routeActions.fillRouteDescription(data.routeInfo.desc);
        routeActions.clickOnRouteNextBtn();
        routeActions.clickOnScheduleTab('Hourly');
        routeActions.clickOnFrequency('Hour', 0);
        routeActions.clickOnFrequency('Minute', 2);
        // test connectivity
        routeActions.clickOnTestConnectivity();
        routeActions.verifyTestConnectivityLogs(data.routeInfo.testConnectivityMessage);
        routeActions.closeTestConnectivity();

        routeActions.setScheduleStartDate();
        routeActions.clickOnCreateRouteBtn();

        dataSourcesPage.clickOnCreatedChannelName(data.channelInfo.channelName);
        // verify route details
        dataSourcesPage.verifyRouteDetails(data.routeInfo, true);
        dataSourcesPage.clickOnRouteAction(data.routeInfo.routeName);
        dataSourcesPage.clickOnDeleteRoute();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyRouteDeleted(data.routeInfo.routeName);
        // delete the channel
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS_APIPULL',
        dataProvider: 'createAndDeleteRoute'
      };
    }
  );
});
