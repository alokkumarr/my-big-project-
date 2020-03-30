const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const commonFunctions = require('../../pages/utils/commonFunctions');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const Constants = require('../../helpers/Constants');
const users = require('../../helpers/data-generation/users');

describe('Executing create and delete geolocation analysis tests from geolocation/createAndDelete.test.js', () => {
  let analysisId;
  let host;
  let token;
  const metricName = dataSets.pivotChart;
  const metrics = 'Double';
  const dimension = 'State';
  // Map
  const dataCol = 'Integer';
  const coordinates = 'COORDINATES';
  const region = 'United States of America';

  beforeAll(() => {
    logger.info('Starting geolocation/createAndDelete.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(
      host,
      users.admin.loginId,
      users.anyUser.password
    );
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          analysisId
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['CREATEDELETECGEO']['geolocation']
      ? testDataReader.testData['CREATEDELETECGEO']['geolocation']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
        const chartName = `e2e ${now}`;
        const chartDescription = `e2e chart description ${now}`;

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);

        const analyzePage = new AnalyzePage();
        analyzePage.clickOnAddAnalysisButton();
        // Click on geolocation
        analyzePage.clickOnAnalysisType();
        analyzePage.clickOnChartType(data.type);
        analyzePage.clickOnNextButton();
        analyzePage.clickOnDataPods(metricName);
        analyzePage.clickOnCreateButton();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.searchInputPresent();

        if (data.type === 'map:chart_scale') {
          chartDesignerPage.clickOnAttribute(metrics, 'Metric');
          chartDesignerPage.clickOnAttribute(dimension, 'Dimension');
          // If gelocation type is map:chart_scale then select the region
          chartDesignerPage.clickOnDataOptionsTab();
          chartDesignerPage.clickOnFieldsByName(dimension);
          chartDesignerPage.selectRegion(region);
        } else {
          chartDesignerPage.clickOnAttribute(dataCol, 'Data');
          chartDesignerPage.clickOnAttribute(coordinates, 'Coordinates');
        }
        //Save
        chartDesignerPage.clickOnSave();
        chartDesignerPage.enterAnalysisName(chartName);
        chartDesignerPage.enterAnalysisDescription(chartDescription);
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

        // Verify analysis displayed in list and card view
        analyzePage.goToView('list');
        analyzePage.verifyElementPresent(
          analyzePage._analysisTitleLink(chartName),
          true,
          'report should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(chartName);

        const executePage = new ExecutePage();
        executePage.verifyTitle(chartName);
        executePage.getAnalysisId().then(id => {
          analysisId = id;
        });

        executePage.clickOnActionLink();
        executePage.clickOnDetails();
        executePage.verifyDescription(chartDescription);
        executePage.closeActionMenu();
        // Delete the report
        executePage.clickOnActionLink();
        executePage.clickOnDelete();
        executePage.confirmDelete();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage.verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEDELETECGEO',
        dataProvider: 'geolocation'
      };
    }
  );
});
