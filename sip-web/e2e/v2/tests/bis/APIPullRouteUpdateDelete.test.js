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

describe('BIS API PULL tests: APIPullRouteUpdateDelete.test.js', () => {
  beforeAll(function() {
    logger.info(`started executing BIS API PULL tests APIPullRouteUpdateDelete.test.js`);
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
    logger.info(`completed executing BIS API PULL tests APIPullRouteUpdateDelete.test.js`);
  });

  using(
    testDataReader.testData['BIS_APIPULL']['updateAndDeleteRoute']
      ? testDataReader.testData['BIS_APIPULL']['updateAndDeleteRoute']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.warn(`Running testCase with id: ${id}`);
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

        // update the route
        let updatedRouteInfo = Object.assign({}, data.routeInfo);
        updatedRouteInfo.routeName = `${updatedRouteInfo.routeName}${Utils.randomId()}up`;
        updatedRouteInfo.desc = `${updatedRouteInfo.routeName} udapted description ${Utils.randomId()}`;
        updatedRouteInfo.destination = `/destup${time}`;

        dataSourcesPage.clickOnRouteAction(data.routeInfo.routeName);
        dataSourcesPage.clickOnEditRoute(data.routeInfo.routeName);
        routeActions.fillApiRouteName(updatedRouteInfo.routeName);
        routeActions.fillApiRouteDestination(updatedRouteInfo.destination);
        channelActions.selectMethodType(updatedRouteInfo.method);
        channelActions.fillEndPoint(updatedRouteInfo.endPoint);
        if (updatedRouteInfo.method === 'POST') channelActions.fillRequestBody(JSON.stringify(updatedRouteInfo.body));
        if (updatedRouteInfo.headers) {
          channelActions.clearHeader();
          channelActions.addHeaders(updatedRouteInfo.headers);
        }
        if (updatedRouteInfo.queryParams) {
          channelActions.clearQueryParams();
          channelActions.addQueryParams(updatedRouteInfo.queryParams);
        }
        routeActions.fillRouteDescription(updatedRouteInfo.desc);
        routeActions.clickOnRouteNextBtn();
        routeActions.clickOnScheduleTab('Hourly');
        routeActions.clickOnFrequency('Hour', 1);
        routeActions.clickOnFrequency('Minute', 3);
        // test connectivity
        routeActions.clickOnTestConnectivity();
        routeActions.verifyTestConnectivityLogs(updatedRouteInfo.testConnectivityMessage);
        routeActions.closeTestConnectivity();

        routeActions.setScheduleStartDate();
        routeActions.clickOnUpdateRoute();

        // verify updated route details
        dataSourcesPage.verifyRouteDetails(updatedRouteInfo, true);
        dataSourcesPage.clickOnRouteAction(updatedRouteInfo.routeName);
        dataSourcesPage.clickOnDeleteRoute();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyRouteDeleted(updatedRouteInfo.routeName);
        // delete the channel
        dataSourcesPage.clickOnDeleteChannel();
        dataSourcesPage.clickOnConfirmYesButton();
        dataSourcesPage.verifyChannelNotDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'BIS_APIPULL',
        dataProvider: 'updateAndDeleteRoute'
      };
    }
  );
});
