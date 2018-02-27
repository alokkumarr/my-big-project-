const loginPage = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const homePage = require('../../javascript/pages/homePage.po.js');
const protractor = require('protractor');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const {hasClass} = require('../../javascript/helpers/utils');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');

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
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('Should apply filter to Pivot', () => {
    loginPage.loginAs('admin');
    commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

    // Create Pivot
    homePage.createAnalysis(metricName, analysisType);

    // Apply filters
    const filters = analyzePage.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = columnField;

    commonFunctions.waitFor.elementToBeClickableAndClick(pivotDesigner.openFiltersBtn);
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    commonFunctions.waitFor.elementToBeClickableAndClick(filters.applyBtn);
    const filterName = filters.getAppliedFilter(fieldName);

    commonFunctions.waitFor.elementToBePresent(filterName);
    expect(filterName.isPresent()).toBe(true);

    // Should select row, column and data fields and refresh data
    const refreshBtn = pivotDesigner.refreshBtn;

    commonFunctions.waitFor.elementToBeClickableAndClick(pivotDesigner.getPivotFieldCheckbox(dataField));
    pivotDesigner.doSelectPivotArea(dataField, 'data');
    pivotDesigner.doSelectPivotAggregate(dataField, 'sum');

    commonFunctions.waitFor.elementToBeClickableAndClick(pivotDesigner.getPivotFieldCheckbox(columnField));
    pivotDesigner.doSelectPivotArea(columnField, 'column');

    commonFunctions.waitFor.elementToBeClickableAndClick(pivotDesigner.getPivotFieldCheckbox(rowField));
    pivotDesigner.doSelectPivotArea(rowField, 'row');

    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    commonFunctions.waitFor.elementToBeClickableAndClick(refreshBtn);

    //Save report
    const save = analyzePage.saveDialog;
    const designer = analyzePage.designerDialog;
    commonFunctions.waitFor.elementToBeClickableAndClick(designer.saveBtn);

    expect(designer.saveDialog).toBeTruthy();

    save.nameInput.clear().sendKeys(pivotName);
    save.descriptionInput.clear().sendKeys(pivotDescription);
    commonFunctions.waitFor.elementToBeClickableAndClick(save.saveBtn);
    commonFunctions.waitFor.elementToBePresent(analyzePage.main.getCardTitle(pivotName))
      .then(() => expect(analyzePage.main.getCardTitle(pivotName).isPresent()).toBe(true));

    // Delete Pivot
    const main = analyzePage.main;
    main.getAnalysisCards(pivotName).count()
      .then(count => {
        main.doAnalysisAction(pivotName, 'delete');
        commonFunctions.waitFor.elementToBeClickableAndClick(main.confirmDeleteBtn);
        expect(main.getAnalysisCards(pivotName).count()).toBe(count - 1);
      });
  });
});
