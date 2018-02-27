const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const homePage = require('../javascript/pages/homePage.po.js');
const protractor = require('protractor');
const protractorConf = require('../../../../saw-web/conf/protractor.conf');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');

describe('Create report type analysis: createReport.test.js', () => {
  const reportDesigner = analyzePage.designerDialog.report;
  const reportName = `e2e report ${(new Date()).toString()}`;
  const reportDescription = 'e2e report description';
  const tables = [{
    name: 'MCT_DN_SESSION_SUMMARY',
    fields: [
      'Available (MB)',
      'Source OS',
      'Source Model'
    ]
  }/*, {
    name: 'MCT_CONTENT_SUMMARY',
    fields: [
      'Available Items'
    ]
  }*/];
  /*const join = {
    tableA: tables[0].name,
    fieldA: 'Session Id',
    tableB: tables[1].name,
    fieldB: 'Session Id'
  };*/
  const filterValue = 'ANDROID';
  const metricName = 'MCT TMO Session DL';
  const analysisType = 'table:report';

  beforeAll(function () {
    // This test may take some time. Such timeout fixes jasmine DEFAULT_TIMEOUT_INTERVAL interval error
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

    // Waiting for results may take some time
    browser.manage().timeouts().implicitlyWait(protractorConf.timeouts.extendedImplicitlyWait);
  });

  beforeEach(function (done) {
    setTimeout(function () {
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('Should apply filter to Report', () => {
    loginPage.loginAs('admin');

    // Switch to Card View
    commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

    // Create Report
    homePage.createAnalysis(metricName, analysisType);

    browser.waitForAngularEnabled(false);
    /*element(by.xpath(`//md-checkbox/div/span[text()='Source OS']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    element(by.xpath(`//md-checkbox/div/span[text()='Available (MB)']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    element(by.xpath(`//md-checkbox/div/span[text()='Source Model']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    browser.waitForAngularEnabled(true);*/

    // Select fields and refresh
    tables.forEach(table => {
      table.fields.forEach(field => {
        commonFunctions.waitFor.elementToBeClickableAndClick(reportDesigner.getReportFieldCheckbox(table.name, field));
      });
    });

    /*expect(
      reportDesigner
        .getJoinlabel(join.tableA, join.fieldA, join.tableB, join.fieldB, 'inner')
        .isPresent()
    ).toBe(false);

    const endpointA = reportDesigner.getReportFieldEndPoint(join.tableA, join.fieldA, 'right');
    const endpointB = reportDesigner.getReportFieldEndPoint(join.tableB, join.fieldB, 'left');
    browser.actions().dragAndDrop(endpointA, endpointB).perform();

    expect(
      reportDesigner
        .getJoinlabel(join.tableA, join.fieldA, join.tableB, join.fieldB, 'inner')
        .isPresent()
    ).toBe(true);*/

    commonFunctions.waitFor.elementToBeClickableAndClick(reportDesigner.refreshBtn);

    // Should apply filters
    const filters = analyzePage.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getNumberFilterInput(0);
    const fieldName = tables[0].fields[0];

    commonFunctions.waitFor.elementToBeClickableAndClick(reportDesigner.openFiltersBtn);
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys("123");
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    commonFunctions.waitFor.elementToBeClickableAndClick(filters.applyBtn);

    const appliedFilter = filters.getAppliedFilter(fieldName);
    commonFunctions.waitFor.elementToBePresent(appliedFilter);
    expect(appliedFilter.isPresent()).toBe(true);

    // Save
    const save = analyzePage.saveDialog;
    const designer = analyzePage.designerDialog;
    commonFunctions.waitFor.elementToBeClickableAndClick(designer.saveBtn);

    commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
    expect(designer.saveDialog).toBeTruthy();

    save.nameInput.clear().sendKeys(reportName);
    save.descriptionInput.clear().sendKeys(reportDescription);
    commonFunctions.waitFor.elementToBeClickableAndClick(save.saveBtn);

    const createdAnalysis = analyzePage.main.getCardTitle(reportName);

    commonFunctions.waitFor.elementToBePresent(createdAnalysis)
      .then(() => expect(createdAnalysis.isPresent()).toBe(true));

    // Delete
    const main = analyzePage.main;
    const cards = main.getAnalysisCards(reportName);
    main.getAnalysisCards(reportName).count()
      .then(count => {
        main.doAnalysisAction(reportName, 'delete');
        commonFunctions.waitFor.elementToBeClickableAndClick(main.confirmDeleteBtn);
        commonFunctions.waitFor.cardsCountToUpdate(cards, count);
        expect(main.getAnalysisCards(reportName).count()).toBe(count - 1);
      });
  });
});
