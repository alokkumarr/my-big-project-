const login = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const designModePage = require('../../javascript/pages/designModePage.po.js');
const homePage = require('../../javascript/pages/homePage.po.js');
const protractor = require('protractor');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const utils = require('../../javascript/helpers/utils');
const dataSets = require('../../javascript/data/datasets');

describe('Apply filters to chart: applyFiltersToCharts.js', () => {
  const chartDesigner = analyzePage.designerDialog.chart;
  const yAxisName = 'Integer';
  const xAxisName = 'String';
  const filterValue = '123';
  const groupName = 'Date';
  const metricName = dataSets.pivotChart;
  const analysisType = 'chart:column';

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
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
    //commonFunctions.logOutByClearingLocalStorage();
  });

  it('Should apply filter to column chart', () => { // SAWQA-174
    login.loginAs('admin');

    // Create analysis
    homePage.createAnalysis(metricName, analysisType);

    // Select axis and grouping and refresh
    const refreshBtn = chartDesigner.refreshBtn;
    // Wait for field input box.
    commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);

    // Search field and add that into metric section.
    commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName));
    designModePage.chart.addFieldButton(yAxisName).click();

    // Search field and add that into dimension section.
    commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(xAxisName));
    designModePage.chart.addFieldButton(xAxisName).click();
    // Refresh button is removed as part of 3363
    // const doesDataNeedRefreshing = utils.hasClass(refreshBtn, 'mat-primary');
    // expect(doesDataNeedRefreshing).toBeTruthy();

    // Search field and add that into group by section.
    commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(groupName));
    designModePage.chart.addFieldButton(groupName).click();

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
    // Refresh button is removed as part of 3363
    // commonFunctions.waitFor.elementToBeClickableAndClick(refreshBtn);

    // Apply filters
    const filters = analyzePage.filtersDialogUpgraded;
    const filterAC = filters.getFilterAutocomplete(0);
    const fieldName = yAxisName;
    commonFunctions.waitFor.elementToBeClickable(chartDesigner.filterBtn);
    chartDesigner.filterBtn.click();

    commonFunctions.waitFor.elementToBeClickable(designModePage.filterWindow.addFilter('sample'));
    designModePage.filterWindow.addFilter('sample').click();

    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    designModePage.filterWindow.numberInputUpgraded.sendKeys(filterValue);
    commonFunctions.waitFor.elementToBeEnabledAndVisible(filters.applyBtn);
    commonFunctions.waitFor.elementToBeClickable(filters.applyBtn);
    filters.applyBtn.click();

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
