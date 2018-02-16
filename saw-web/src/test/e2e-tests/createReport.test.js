const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
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
  const metric = 'MCT TMO Session DL';
  const method = 'table:report';

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
    commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
    analyzePage.analysisElems.cardView.click();

    // Create Report
    commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.addAnalysisBtn);
    analyzePage.analysisElems.addAnalysisBtn.click();
    const newDialog = analyzePage.newDialog;
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();

    browser.waitForAngularEnabled(false);
    /*element(by.xpath(`//md-checkbox/div/span[text()='Source OS']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    element(by.xpath(`//md-checkbox/div/span[text()='Available (MB)']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    element(by.xpath(`//md-checkbox/div/span[text()='Source Model']/ancestor::*[contains(@e2e, 'MCT_DN_SESSION_SUMMARY')]`)).click();
    browser.waitForAngularEnabled(true);*/

    // Select fields and refresh
    tables.forEach(table => {
      table.fields.forEach(field => {
        reportDesigner.getReportFieldCheckbox(table.name, field).click();
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

    reportDesigner.refreshBtn.click();

    // Should apply filters
    const filters = analyzePage.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getNumberFilterInput(0);
    const fieldName = tables[0].fields[0];

    commonFunctions.waitFor.elementToBeClickable(reportDesigner.openFiltersBtn);
    reportDesigner.openFiltersBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys("123");
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    filters.applyBtn.click();

    const appliedFilter = filters.getAppliedFilter(fieldName);
    commonFunctions.waitFor.elementToBePresent(appliedFilter);
    expect(appliedFilter.isPresent()).toBe(true);

    // Save
    const save = analyzePage.saveDialog;
    const designer = analyzePage.designerDialog;
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    designer.saveBtn.click();

    commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
    expect(designer.saveDialog).toBeTruthy();

    save.nameInput.clear().sendKeys(reportName);
    save.descriptionInput.clear().sendKeys(reportDescription);
    save.saveBtn.click();

    const createdAnalysis = analyzePage.main.getCardTitle(reportName);

    commonFunctions.waitFor.elementToBePresent(createdAnalysis)
      .then(() => expect(createdAnalysis.isPresent()).toBe(true));

    // Delete
    const main = analyzePage.main;
    const cards = main.getAnalysisCards(reportName);
    main.getAnalysisCards(reportName).count()
      .then(count => {
        main.doAnalysisAction(reportName, 'delete');
        main.confirmDeleteBtn.click();
        commonFunctions.waitFor.cardsCountToUpdate(cards, count);
        expect(main.getAnalysisCards(reportName).count()).toBe(count - 1);
      });
  });
});
