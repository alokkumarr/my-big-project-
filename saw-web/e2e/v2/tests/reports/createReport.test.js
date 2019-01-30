const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const Header = require('../../pages/components/Header');
const AnalyzePage = require('../../pages/AnalyzePage');
const commonFunctions = require('../../pages/utils/commonFunctions');

describe('Executing create report tests from createReport.test.js', () => {
  const reportDesigner = analyzePage.designerDialog.report;
  const reportName = `e2e report ${new Date().toString()}`;
  const reportDescription = 'e2e report description';
  const tables = [
    {
      name: 'SALES',
      fields: ['Integer', 'String', 'Date']
    }
  ];

  const filterOperator = 'Equal to';
  const filterValue = '123';
  const metricName = dataSets.report;
  const analysisType = 'table:report';

  beforeAll(() => {
    logger.info('Starting createReport tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['CREATEREPORT']['positiveTests']
      ? testDataReader.testData['CREATEREPORT']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        let loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);
        let header = new Header();
        header.verifyLogo();

        let analyzePage = new AnalyzePage();
        analyzePage.goToView('card');
        analyzePage.clickOnAddAnalysisButton();
        analyzePage.clickOnAnalysisType(analysisType);
        analyzePage.clickOnNextButton();
        analyzePage.clickOnDataPods(metricName);
        analyzePage.clickOnCreateButton();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEREPORT',
        dataProvider: 'positiveTests'
      };
    }
  );
});
