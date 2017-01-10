var login = require('../pages/common/login.po.js');
var header = require('../pages/components/header.co.js');
var sidenav = require('../pages/components/sidenav.co.js');
var analyze = require('../pages/common/analyze.po.js');


describe('Analyses Tests', function () {

/*  it('should login', function () {
    //browser.sleep(2000);
    login.loginElements.loginBtn.click();
  });*/

  it('should navigate to Analyze page', function () {
    header.headerElements.analyzeBtn.click();
    expect(browser.getCurrentUrl()).toContain('/analyze');
  });

  it('should open the sidenav menu and go to first category', function () {
    sidenav.sidenavElements.menuBtn.click();
    sidenav.sidenavElements.myAnalyses.click();
    sidenav.sidenavElements.firstCategory.click();
  });

  it('should see the analysis card view', function () {
    analyze.validateCard();
  });

  it('should attempt to create a new analysis', function () {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateAnalyzeDialog();
  });
});
