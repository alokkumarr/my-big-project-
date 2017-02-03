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

  it('should add fields to report', () => {

  });

  it('should attempt to save the report and fill the details', () => {
    analyze.analysisElems.saveReportBtn.click();
    analyze.analysisElems.reportCategory.click();
    analyze.analysisElems.firstCategoryOption.click();
    expect(analyze.analysisElems.reportCategory.getText()).toEqual('Order Fulfillment');
    analyze.analysisElems.reportName.clear().sendKeys('e2e report');
    analyze.analysisElems.reportDescription.clear().sendKeys('e2e test description');
    analyze.analysisElems.saveReportDetails.click();
  });

  it('should change the report details accordingly ', () => {
    expect(analyze.analysisElems.reportTitle.getText()).toEqual('e2e report');
    analyze.analysisElems.reportDescriptionBtn.click();
    expect(analyze.analysisElems.reportDescription.getText()).toEqual('e2e test description');
  });
});
