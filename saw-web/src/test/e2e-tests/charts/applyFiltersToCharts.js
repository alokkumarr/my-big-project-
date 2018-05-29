const login = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const designModePage = require('../../javascript/pages/designModePage.po.js');
const homePage = require('../../javascript/pages/homePage.po.js');
const protractor = require('protractor');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const utils = require('../../javascript/helpers/utils');

describe('Apply filters to chart: applyFiltersToCharts.js', () => {
  const chartDesigner = analyzePage.designerDialog.chart;
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const filterValue = '123';
  const groupName = 'Source OS';
  const metricName = 'MCT TMO Session ES';
  const analysisType = 'chart:column';

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      commonFunctions.logOutByClearingLocalStorage();
      commonFunctions.openBaseUrl();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('Should apply filter to column chart', () => {
    login.loginAs('admin');

    // Create analysis
    homePage.createAnalysis(metricName, analysisType);

    // Select axis and grouping and refresh
    const refreshBtn = chartDesigner.refreshBtn;
    // Wait for field input box.
    commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);
    // Search field and add that into dimension section.
    // analyzePage.designerDialog.chart.fieldSearchInput.clear();
    analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(xAxisName);
    commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(xAxisName));

    const doesDataNeedRefreshing = utils.hasClass(refreshBtn, 'mat-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();

    // Search field and add that into group by section.
    // analyzePage.designerDialog.chart.fieldSearchInput.clear();
    analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(groupName);
    commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(groupName));
    // Search field and add that into metric section.
    // analyzePage.designerDialog.chart.fieldSearchInput.clear();
    analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(yAxisName);
    commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(yAxisName));

    // Check selected field is present in respective section.
    let y = analyzePage.designerDialog.chart.getMetricsFields(yAxisName);
    commonFunctions.waitFor.elementToBeVisible(y);
    expect(y.isDisplayed()).toBeTruthy();
    let x = analyzePage.designerDialog.chart.getDimensionFields(xAxisName);
    commonFunctions.waitFor.elementToBeVisible(x);
    expect(x.isDisplayed()).toBeTruthy();
    let g = analyzePage.designerDialog.chart.getGroupByFields(groupName);
    commonFunctions.waitFor.elementToBeVisible(g);
    expect(g.isDisplayed()).toBeTruthy();

    commonFunctions.waitFor.elementToBeClickableAndClick(refreshBtn);

    // Apply filters
    const filters = analyzePage.filtersDialogUpgraded;
    const filterAC = filters.getFilterAutocomplete(0);
    const fieldName = yAxisName;

    commonFunctions.waitFor.elementToBeClickableAndClick(chartDesigner.filterBtn);
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    designModePage.filterWindow.numberInputUpgraded.sendKeys(filterValue);
    commonFunctions.waitFor.elementToBeEnabledAndVisible(filters.applyBtn);
    commonFunctions.waitFor.elementToBeClickableAndClick(filters.applyBtn);

    //TODO: Need to check that filters applied or not.
    commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.filterText);
    commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.filterClear);
    commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.selectedFiltersText);
    validateSelectedFilters([fieldName]);

  });

  const validateSelectedFilters = (filters) => {

    analyzePage.appliedFiltersDetails.selectedFilters.map(function(elm) {
      return elm.getText();
    }).then(function(displayedFilters) {
      expect(utils.arrayContainsArray(displayedFilters, filters)).toBeTruthy();
    });
  };

});
