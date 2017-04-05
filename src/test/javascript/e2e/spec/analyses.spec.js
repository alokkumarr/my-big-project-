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

  it('should verify the first metric', () => {
    analyze.analysisElems.firstMetric.click();
    expect(analyze.analysisElems.secondMetric.getAttribute('aria-disabled')).toEqual('true');
    analyze.analysisElems.firstMetric.click();
    expect(analyze.analysisElems.secondMetric.getAttribute('aria-disabled')).toEqual('false');
  });

  it('should select Report type and proceed', () => {
    analyze.analysisElems.reportType.click();
    analyze.analysisElems.createAnalysisBtn.click();
    analyze.validateDesignerView();
  });

  it('should display the added fields to report details', () => {
    analyze.analysisElems.ordersOrderNumber.click();
    analyze.analysisElems.customersEmail.click();
    analyze.analysisElems.productsProductTypes.click();
    analyze.analysisElems.serviceProductStatus.click();
    analyze.analysisElems.toggleDetailsPanel.click();
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
    analyze.analysisElems.saveReportBtn.click();
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

  it('should verify the first metric', () => {
    analyze.analysisElems.firstMetric.click();
    expect(analyze.analysisElems.secondMetric.getAttribute('aria-disabled')).toEqual('true');
    analyze.analysisElems.firstMetric.click();
    expect(analyze.analysisElems.secondMetric.getAttribute('aria-disabled')).toEqual('false');
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
    analyze.analysisElems.saveReportBtn.click();
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
