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

  it('should card view by default', () => {
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

  it('should attempt to save the report', () => {
    analyze.analysisElems.saveReportBtn.click();
  });
});
