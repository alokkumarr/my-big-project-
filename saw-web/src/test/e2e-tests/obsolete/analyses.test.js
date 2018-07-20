const header = require('../../javascript/pages/components/header.co.js');
const sidenav = require('../../javascript/pages/components/sidenav.co.js');
const analyze = require('../../javascript/pages/analyzePage.po.js');

// Obsolete. Do not delete. Use some in future
describe('Analyses Tests', () => {
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

  it('should select report type and proceed', () => {
    analyze.analysisElems.reportTable.click();
    analyze.analysisElems.createAnalysisBtn.click();
    analyze.validateDesignerDialog();
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
