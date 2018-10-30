const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const homePage = require('../javascript/pages/homePage.po.js');
const protractor = require('protractor');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const {hasClass} = require('../javascript/helpers/utils');
const protractorConf = require('../../protractor.conf');

describe('Create pivot type analysis: createPivot.test.js', () => {
  const pivotDesigner = analyzePage.designerDialog.pivot;
  const pivotName = `e2e pivot${(new Date()).toString()}`;
  const pivotDescription = 'e2e pivot description';
  const dataField = 'Available MB';
  const filterValue = 'SAMSUNG';
  const columnField = 'Source Manufacturer';
  const rowField = 'Source OS';
  const metricName = 'MCT TMO Session ES';
  const analysisType = 'table:pivot';

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      //expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    //commonFunctions.logOutByClearingLocalStorage();
  });

  it('Should apply filter to Pivot', () => { // SAW-3894
    loginPage.loginAs('admin');
    commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
    commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
    analyzePage.analysisElems.cardView.click();

    // Create Pivot
    homePage.createAnalysis(metricName, analysisType);

    // Apply filters
    const filters = analyzePage.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = columnField;

    commonFunctions.waitFor.elementToBeVisible(pivotDesigner.filterBtn);
    commonFunctions.waitFor.elementToBeClickable(pivotDesigner.filterBtn);
    pivotDesigner.filterBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    commonFunctions.waitFor.elementToBeVisible(filters.applyBtn);
    commonFunctions.waitFor.elementToBeClickable(filters.applyBtn);
    filters.applyBtn.click();

    const filterName = filters.getAppliedFilter(fieldName);

    commonFunctions.waitFor.elementToBePresent(filterName);
    expect(filterName.isPresent()).toBe(true);

    // Should select row, column and data fields and refresh data
    const refreshBtn = pivotDesigner.refreshBtn;

    commonFunctions.waitFor.elementToBeVisible(pivotDesigner.getPivotFieldCheckbox(dataField));
    commonFunctions.waitFor.elementToBeClickable(pivotDesigner.getPivotFieldCheckbox(dataField));
    pivotDesigner.getPivotFieldCheckbox(dataField).click();
    pivotDesigner.doSelectPivotArea(dataField, 'data');
    pivotDesigner.doSelectPivotAggregate(dataField, 'sum');

    commonFunctions.waitFor.elementToBeVisible(pivotDesigner.getPivotFieldCheckbox(columnField));
    commonFunctions.waitFor.elementToBeClickable(pivotDesigner.getPivotFieldCheckbox(columnField));
    pivotDesigner.getPivotFieldCheckbox(columnField).click();
    pivotDesigner.doSelectPivotArea(columnField, 'column');

    commonFunctions.waitFor.elementToBeVisible(pivotDesigner.getPivotFieldCheckbox(rowField));
    commonFunctions.waitFor.elementToBeClickable(pivotDesigner.getPivotFieldCheckbox(rowField));
    pivotDesigner.getPivotFieldCheckbox(rowField).click();
    pivotDesigner.doSelectPivotArea(rowField, 'row');

    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    commonFunctions.waitFor.elementToBeVisible(refreshBtn);
    commonFunctions.waitFor.elementToBeClickable(refreshBtn);
    refreshBtn.click();

    //Save report
    const save = analyzePage.saveDialog;
    const designer = analyzePage.designerDialog;
    commonFunctions.waitFor.elementToBeVisible(designer.saveBtn);
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    designer.saveBtn.click();

    expect(designer.saveDialog).toBeTruthy();

    save.nameInput.clear().sendKeys(pivotName);
    save.descriptionInput.clear().sendKeys(pivotDescription);
    commonFunctions.waitFor.elementToBeVisible(save.saveBtn);
    commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
    save.saveBtn.click();
    commonFunctions.waitFor.elementToBePresent(analyzePage.main.getCardTitle(pivotName))
      .then(() => expect(analyzePage.main.getCardTitle(pivotName).isPresent()).toBe(true));

    // Delete Pivot
    const main = analyzePage.main;
    main.getAnalysisCards(pivotName).count()
      .then(count => {
        main.doAnalysisAction(pivotName, 'delete');
        commonFunctions.waitFor.elementToBeVisible(main.confirmDeleteBtn);
        commonFunctions.waitFor.elementToBeClickable(main.confirmDeleteBtn);
        main.confirmDeleteBtn.click();
        expect(main.getAnalysisCards(pivotName).count()).toBe(count - 1);
      });
  });
});
