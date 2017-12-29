const login = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const protractor = require('protractor');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const {hasClass} = require('../../javascript/helpers/utils');

describe('Apply filters to charts: applyFiltersToCharts.js', () => {
  const chartDesigner = analyzePage.designerDialog.chart;
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const filterValue = 'APPLE';
  const groupName = 'Source OS';
  const metric = 'MCT TMO Session ES';
  const method = 'chart:column';

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000000;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, 1000)
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, 1000)
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('Should apply filter to column chart', () => {
    login.loginAs('admin');

    // Switch to Card view
    commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
    analyzePage.analysisElems.cardView.click();

    // Add analysis
    analyzePage.analysisElems.addAnalysisBtn.click();
    const newDialog = analyzePage.newDialog;
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();

    // Select axis and grouping and refresh
    const refreshBtn = chartDesigner.refreshBtn;
    const x = chartDesigner.getXRadio(xAxisName);
    const y = chartDesigner.getYCheckBox(yAxisName);
    const g = chartDesigner.getGroupRadio(groupName);
    x.click();
    commonFunctions.waitFor.elementToBeClickable(y);
    y.click();
    g.click();
    expect(hasClass(x, 'md-checked')).toBeTruthy();
    expect(hasClass(y, 'md-checked')).toBeTruthy();
    expect(hasClass(g, 'md-checked')).toBeTruthy();
    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    refreshBtn.click();

    // Apply filters
    const filters = analyzePage.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = xAxisName;

    chartDesigner.openFiltersBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    filters.applyBtn.click();

    const appliedFilter = filters.getAppliedFilter(fieldName);
    commonFunctions.waitFor.elementToBePresent(appliedFilter);
    expect(appliedFilter.isPresent()).toBe(true);
  });
});
