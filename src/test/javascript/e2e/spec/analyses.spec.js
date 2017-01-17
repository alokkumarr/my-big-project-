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

  it('should see the analysis card view', () => {
    analyze.validateCard();
  });

  it('should attempt to create a new analysis', () => {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateAnalyzeDialog();
  });

  it('should select Report table type and click Create', () => {
    analyze.analysisElems.analyzeReportTable.click();
    analyze.analysisElems.createAnalysisBtn.click();
  });

  it('should open the designer dialog', () => {
    analyze.validateAnalyzeDialog();
  });
});
