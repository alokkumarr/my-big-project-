const header = require('../pages/components/header.co.js');
const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');

describe('Report Analysis Tests', () => {
  it('should navigate to Analyze page', () => {
    header.headerElements.analyzeBtn.click();
    expect(browser.getCurrentUrl()).toContain('/analyze');
  });

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
    analyze.analysisElems.sessionIdField.click();
    analyze.analysisElems.toggleDetailsPanel.click();
    analyze.analysisElems.refreshDataBtn.click();
    browser.wait(10000);
    analyze.validateReportGrid();
  });

  it('should apply filters', () => {
    analyze.analysisElems.reportFilterBtn.click();
    analyze.analysisElems.filterItemInternet.click();
    analyze.analysisElems.filterItemComplete.click();
    analyze.analysisElems.applyFilterBtn.click();
    analyze.validateReportFilters();
  });

  it('should attempt to save the report and fill the details', () => {
    analyze.analysisElems.saveAnalysisBtn.click();
    expect(analyze.analysisElems.reportCategory.getText()).toEqual('Order Fulfillment');
    analyze.analysisElems.reportNameField.clear().sendKeys('e2e report');
    analyze.analysisElems.reportDescriptionField.clear().sendKeys('e2e test description');
    analyze.analysisElems.saveReportDetails.click();
  });

  it('should change the report details accordingly ', () => {
    expect(analyze.analysisElems.reportTitle.getText()).toEqual('e2e report');
    analyze.analysisElems.reportDescriptionBtn.click();
    expect(analyze.analysisElems.reportDescription.getAttribute('value')).toEqual('e2e test description');
  });
});

describe('Column Chart Analysis Tests', () => {
  it('should navigate to Analyze page', () => {
    browser.driver.get('http://localhost:3000');
    header.headerElements.analyzeBtn.click();
    expect(browser.getCurrentUrl()).toContain('/analyze');
  });

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

  it('should attempt to save the report and fill the details', () => {
    analyze.analysisElems.saveAnalysisBtn.click();
    expect(analyze.analysisElems.reportCategory.getText()).toEqual('Order Fulfillment');
    analyze.analysisElems.reportNameField.clear().sendKeys('e2e column chart');
    analyze.analysisElems.reportDescriptionField.clear().sendKeys('e2e test description');
    analyze.analysisElems.saveReportDetails.click();
  });

  it('should change the chart details accordingly ', () => {
    expect(analyze.analysisElems.reportTitle.getText()).toEqual('e2e column chart');
    analyze.analysisElems.reportDescriptionBtn.click();
    expect(analyze.analysisElems.reportDescription.getAttribute('value')).toEqual('e2e test description');
  });
});
