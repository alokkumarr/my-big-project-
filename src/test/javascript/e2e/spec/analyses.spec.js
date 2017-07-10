const header = require('../pages/components/header.co.js');
const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');

describe('Analyses Tests', () => {
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

  it('should have the Report method enabled when selecting the last analysis', () => {
    analyze.analysisElems.lastMetric.click();
    expect(analyze.analysisElems.reportTable.getAttribute('disabled')).toEqual(null);
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
