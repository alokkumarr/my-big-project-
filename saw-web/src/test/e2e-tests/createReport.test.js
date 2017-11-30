const login = require('../javascript/pages/common/login.po.js');
const sidenav = require('../javascript/pages/components/sidenav.co.js');
const analyze = require('../javascript/pages/common/analyzePage.po.js');
const protractor = require('protractor');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');

describe('create a new report type analysis: createReport.test.js', () => {
  let categoryName;
  const reportDesigner = analyze.designerDialog.report;
  const reportName = `e2e report ${(new Date()).toString()}`;
  const reportDescription = 'e2e report description';
  const tables = [{
    name: 'MCT_DN_SESSION_SUMMARY',
    fields: [
      'Source OS',
      'Available (MB)',
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

  afterAll(function() {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('login as admin', () => {
    expect(browser.getCurrentUrl()).toContain('/login');
    login.loginAs('admin');
  });

  //Obsolete. Now menu opens automatically with first category expanded
  /* it('should open the sidenav menu and go to first category', () => {
    commonFunctions.waitFor.elementToBeClickable(sidenav.menuBtn);
    sidenav.menuBtn.click();
    sidenav.publicCategoriesToggle.click();
    categoryName = sidenav.firstPublicCategory.getText();
    sidenav.firstPublicCategory.click();
    expect(analyze.main.categoryTitle.getText()).toEqual(categoryName);
  }); */

  it('should display list view by default', () => {
    categoryName = sidenav.firstPublicCategory.getText();
    analyze.validateListView();
  });

  it('should switch to card view', () => {
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
    analyze.analysisElems.cardView.click();
  });

  it('should open the new Analysis dialog', () => {
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.addAnalysisBtn);
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateNewAnalyze();
  });

  it('should select pivot type and proceed', () => {
    const newDialog = analyze.newDialog;
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();
    expect(reportDesigner.title.isPresent()).toBe(true);
  });

  it('should select fields and refresh data', () => {
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
  });

  it('should apply filters', () => {
    const filters = analyze.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = tables[0].fields[0];

    commonFunctions.waitFor.elementToBeClickable(reportDesigner.openFiltersBtn);
    // browser.sleep(100000);
    reportDesigner.openFiltersBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    filters.applyBtn.click();

    const appliedFilter = filters.getAppliedFilter(fieldName);
    commonFunctions.waitFor.elementToBePresent(appliedFilter);
    expect(appliedFilter.isPresent()).toBe(true);
  });

  it('should attempt to save the report', () => {
    const save = analyze.saveDialog;
    const designer = analyze.designerDialog;
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    // browser.actions().mouseMove(designer.saveBtn).click();
    designer.saveBtn.click();

    commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
    expect(designer.saveDialog).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName);

    save.nameInput.clear().sendKeys(reportName);
    save.descriptionInput.clear().sendKeys(reportDescription);
    save.saveBtn.click();

    const createdAnalysis = analyze.main.getCardTitle(reportName);

    commonFunctions.waitFor.elementToBePresent(createdAnalysis)
      .then(() => expect(createdAnalysis.isPresent()).toBe(true));
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    main.getAnalysisCards(reportName).count()
      .then(count => {
        main.doAnalysisAction(reportName, 'delete');
        main.confirmDeleteBtn.click();
        expect(main.getAnalysisCards(reportName).count()).toBe(count - 1);
      });
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
