const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');
const {hasClass} = require('../utils');
const protractor = require('protractor');

const ec = protractor.ExpectedConditions;

const navigateToAnalyze = () => {
  browser.driver.get('http://localhost:3000');
  // the app should automatically navigate to the analyze page
  // and when its on there th ecurrent module link is disabled
  const alreadyOnAnalyzePage = ec.urlContains('/analyze');

  // wait for the app to automatically navigate to the default page
  browser
    .wait(() => alreadyOnAnalyzePage, 1000)
    .then(() => expect(browser.getCurrentUrl()).toContain('/analyze'));
};

describe('Report Analysis Tests', () => {
  it('should navigate to Analyze page', navigateToAnalyze);

  it('should open the sidenav menu and go to first category', () => {
    sidenav.sidenavElements.menuBtn.click();
    sidenav.sidenavElements.cannedAnalysisCategoriesToggle.click();
    sidenav.sidenavElements.firstCannedAnalysisCategory.click();
  });

  it('should display card view by default', () => {
    analyze.validateCardView();
  });

  it('should attempt to create a new analysis', () => {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateNewAnalyze();
  });

  it('should select the Reporting metric', () => {
    analyze.analysisElems.eventsMetric.click();
  });

  it('should select Report type and proceed', () => {
    analyze.analysisElems.reportType.click();
    analyze.analysisElems.createAnalysisBtn.click();
    analyze.validateDesignerView();
  });

  it('should add fields and see the results', () => {
    const refreshDataBtn = analyze.analysisElems.refreshDataBtn;
    analyze.analysisElems.sessionIdField.click();
    analyze.analysisElems.toggleDetailsPanel.click();
    refreshDataBtn.click();
    // when the data is out of synch and needs to be refreshed
    // the button gets the btn-primary class, and is highlighted
    // when the data is refreshed, it looses that class
    const dataRefreshed = ec.not(hasClass(refreshDataBtn, 'btn-primary'));

    // the wait function needs a promise condition to wait for
    browser
      .wait(() => dataRefreshed, 10000)
      .then(() => analyze.validateReportGrid());
  });

  it('should apply filters', () => {
    // this should be updated to the new filters in the filters modal
    // analyze.analysisElems.filterAnalysisBtn.click();

    expect(true).toBe(false);

    /* outDated filters sidenav
    analyze.analysisElems.reportFilterBtn.click();
    analyze.analysisElems.filterItemInternet.click();
    analyze.analysisElems.filterItemComplete.click();
    analyze.analysisElems.applyFilterBtn.click();
    analyze.validateReportFilters();
    */
  });

  const reportName = 'e2e report';
  const reportDescription = 'e2e test report description';

  it('should attempt to save the report and fill the details', () => {
    analyze.analysisElems.saveAnalysisBtn.click();
    expect(analyze.analysisElems.reportCategory.getText()).toEqual('Order Fulfillment');
    analyze.analysisElems.reportNameField.clear().sendKeys(reportName);
    analyze.analysisElems.reportDescriptionField.clear().sendKeys(reportDescription);
    analyze.analysisElems.saveReportDetails.click();
  });

  it('should change the report details accordingly ', () => {
    expect(analyze.analysisElems.reportTitle.getText()).toEqual(reportName);
    analyze.analysisElems.reportDescriptionBtn.click();
    expect(analyze.analysisElems.reportDescription.getAttribute('value')).toEqual(reportDescription);
  });
});

describe('Column Chart Analysis Tests', () => {
  it('should automatically redirect to Analyze page when going to the homepage', navigateToAnalyze);

  it('should open the sidenav menu and go to first category', () => {
    sidenav.sidenavElements.menuBtn.click();
    sidenav.sidenavElements.myAnalyses.click();
    sidenav.sidenavElements.firstCategory.click();
  });

  it('should display card view by default', () => {
    analyze.validateCardView();
  });

  it('should attempt to create a new analysis', () => {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateNewAnalyze();
  });

  it('should select Column Chart type and proceed', () => {
    analyze.analysisElems.columnType.click();
    analyze.analysisElems.createAnalysisBtn.click();
    analyze.validateColumnChartsView();
  });

  it('should display the added fields to chart', () => {
  });

  it('should apply filters', () => {
  });

  const chartName = 'e2e column chart';
  const chartDescription = 'e2e test chart description';

  it('should attempt to save the report and fill the details', () => {
    analyze.analysisElems.saveAnalysisBtn.click();
    expect(analyze.analysisElems.reportCategory.getText()).toEqual('Order Fulfillment');
    analyze.analysisElems.reportNameField.clear().sendKeys(chartName);
    analyze.analysisElems.reportDescriptionField.clear().sendKeys(chartDescription);
    analyze.analysisElems.saveReportDetails.click();
  });

  it('should change the chart details accordingly ', () => {
    expect(analyze.analysisElems.reportTitle.getText()).toEqual(chartName);
    analyze.analysisElems.reportDescriptionBtn.click();
    expect(analyze.analysisElems.reportDescription.getAttribute('value')).toEqual(chartDescription);
  });
});
