const login = require('../javascript/pages/common/login.po.js');
const header = require('../javascript/pages/components/header.co.js');
const analyze = require('../javascript/pages/common/analyzePage.po.js');
const homePage = require('../javascript/pages/homePage.po.js');
const executedAnalysis = require('../javascript/pages/common/executedAlaysis.po');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
const commonFunctions = require('../javascript/helpers/commonFunctions');

function isOptionPresent(options, optionName) {
  const option = analyze.main.getAnalysisOption(options, optionName);
  return option.isPresent();
}

describe('Privileges', () => {

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  describe('for the Admin', () => {
    let analysisOptions;
    it('should login as an admin and open action menu', () => {
      expect(browser.getCurrentUrl()).toContain('/login');
      login.loginAs('admin');
      commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
      analyze.analysisElems.cardView.click();
      analyze.main.getAnalysisActionOptions(analyze.main.firstCard).then(options => {
        analysisOptions = options;
        expect(options.isPresent()).toBe(true);
      });
    });

    it('has fork privilege', () => {
      expect(analyze.main.getForkBtn(analyze.main.firstCard).isDisplayed()).toBe(true);
    });

    it('has execute privilege on card', () => {
      expect(isOptionPresent(analysisOptions, 'execute')).toBe(true);
    });

    it('has edit privilege on card', () => {
      expect(isOptionPresent(analysisOptions, 'edit')).toBe(true);
    });

    it('has publish privilege on card', () => {
      expect(isOptionPresent(analysisOptions, 'publish')).toBe(true);
    });

    it('has delete privilege on card', () => {
      expect(isOptionPresent(analysisOptions, 'delete')).toBe(true);
      commonFunctions.dismissDropdownMenu();
    });

    it('should go to account settings menu: has admin menu option', () => {
      homePage.accountSettingsMenuBtn.click();
      expect(homePage.adminMenuOption.isPresent()).toBe(true);
    });

    it('has password menu option', () => {
      expect(homePage.changePasswordMenuOption.isPresent()).toBe(true);
      commonFunctions.dismissDropdownMenu();

    });

    it('should navigate to executed analysis page', () => {
      analyze.main.firstCardTitle.click();
      commonFunctions.waitFor.pageToBeReady(/executed/);
    });

    it('has edit privilege', () => {
      expect(executedAnalysis.editBtn.isDisplayed()).toBe(true);
    });

    it('has edit&fork privilege', () => {
      expect(executedAnalysis.forkBtn.isDisplayed()).toBe(true);
    });

    it('has execute privilege', () => {
      executedAnalysis.actionsMenuBtn.click();
      expect(executedAnalysis.executeMenuOption.isDisplayed()).toBe(true);
    });

    it('has export privilege', () => {
      expect(executedAnalysis.exportMenuOption.isDisplayed()).toBe(true);
    });

    it('has delete privilege', () => {
      expect(executedAnalysis.deleteMenuOption.isDisplayed()).toBe(true);
    });

    it('has publish privilege', () => {
      expect(executedAnalysis.publishBtn.isDisplayed()).toBe(true);
    });

    it('log out', () => {
      analyze.main.doAccountAction('logout');
    });
  });
});


//TODO refactor test for the user privileges
/*describe('for the User', () => {
  let analysisOptions;
  it('should login as a user and open action menu', () => {
    expect(browser.getCurrentUrl()).toContain('/login');
    login.loginAs('userOne');
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
    analyze.analysisElems.cardView.click();
    analyze.main.getAnalysisActionOptions(analyze.main.firstCard).then(options => {
      analysisOptions = options;
      expect(options.isPresent()).toBe(true);
    });
  });

  it('has fork privilege', () => {
    expect(analyze.main.getForkBtn(analyze.main.firstCard).isDisplayed()).toBe(true);
  });

  it('has execute privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'execute')).toBe(true);
  });

  it('has edit privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'edit')).toBe(true);
  });

  it('has publish privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'publish')).toBe(true);
  });

  it('has delete privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'delete')).toBe(true);
    commonFunctions.dismissDropdownMenu();
  });

  it('should go to account settings menu: has admin menu option', () => {
    homePage.accountSettingsMenuBtn.click();
    expect(homePage.adminMenuOption.isPresent()).toBe(true);
  });

  it('has password menu option', () => {
    expect(homePage.changePasswordMenuOption.isPresent()).toBe(true);
    commonFunctions.dismissDropdownMenu();

  });

  it('should navigate to executed analysis page', () => {
    analyze.main.firstCardTitle.click();
    commonFunctions.waitFor.pageToBeReady(/executed/);
  });

  it('has edit privilege', () => {
    expect(executedAnalysis.editBtn.isDisplayed()).toBe(true);
  });

  it('has edit&fork privilege', () => {
    expect(executedAnalysis.forkBtn.isDisplayed()).toBe(true);
  });

  it('has execute privilege', () => {
    executedAnalysis.actionsMenuBtn.click();
    expect(executedAnalysis.executeMenuOption.isDisplayed()).toBe(true);
  });

  it('has export privilege', () => {
    expect(executedAnalysis.exportMenuOption.isDisplayed()).toBe(true);
  });

  it('has delete privilege', () => {
    expect(executedAnalysis.deleteMenuOption.isDisplayed()).toBe(true);
  });

  it('has publish privilege', () => {
    expect(executedAnalysis.publishBtn.isDisplayed()).toBe(true);
  });

  it('log out', () => {
    analyze.main.doAccountAction('logout');
  });
});*/

describe('for the Analyst', () => {
  it('should land on login page', () => {
    browser.sleep(2000);
    expect(browser.getCurrentUrl()).toContain('/login');
  });

  it('login as analyst', () => {
    login.loginAs('analyst');
  });

  it('should be successfully logged in', () => {
    browser.sleep(2000);
    expect(header.headerElements.companyLogo.isPresent()).toBe(true);
  });

  let analysisOptions;
  it('actions menu exists', () => {
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
    analyze.analysisElems.cardView.click();
    analyze.main.getAnalysisActionOptions(analyze.main.firstCard).then(options => {
      analysisOptions = options;
      expect(options.isPresent()).toBe(true);
    });
  });

  it('has fork privilege', () => {
    expect(analyze.main.getForkBtn(analyze.main.firstCard).isDisplayed()).toBe(true);
  });

  it('has execute privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'execute')).toBe(true);
  });

  it('has no edit privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'edit')).toBe(false);
  });

  it('has publish privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'publish')).toBe(true);
  });

  it('has no delete privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'delete')).toBe(false);
    browser.sleep(1000);
  });

  it('should close the menu', () => {
    // close the opened actions menu
    element(by.css('md-backdrop')).click();
    expect(element(by.css('md-backdrop')).isPresent()).toBe(false);
  });

  it('go to executed analysis page', () => {
    analyze.main.firstCardTitle.click();
    const condition = ec.urlContains('/executed');
    browser
      .wait(() => condition, 1000)
      .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));
  });

  it('should have publish privilege', () => {
    expect(executedAnalysis.publishBtn.isDisplayed()).toBe(true);
  });

  it('should have fork privilege', () => {
    expect(executedAnalysis.forkBtn.isDisplayed()).toBe(true);
  });

  it('should have edit privilege', () => {
    expect(executedAnalysis.editBtn.isDisplayed()).toBe(false);
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});

describe('for Reviewer', () => {
  it('should land on login page', () => {
    browser.sleep(2000);
    expect(browser.getCurrentUrl()).toContain('/login');
  });

  it('login as reviewer', () => {
    login.loginAs('reviewer');
  });

  it('should be successfully logged in', () => {
    browser.sleep(2000);
    expect(header.headerElements.companyLogo.isPresent()).toBe(true);
  });

  let analysisOptions;
  it('actions menu exists', () => {
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
    analyze.analysisElems.cardView.click();
    analyze.main.getAnalysisActionOptions(analyze.main.firstCard).then(options => {
      analysisOptions = options;
      expect(options.isPresent()).toBe(true);
    });
  });

  it('has no fork privilege', () => {
    expect(analyze.main.getForkBtn(analyze.main.firstCard).isDisplayed()).toBe(false);
  });

  it('has execute privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'execute')).toBe(true);
  });

  it('has no edit privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'edit')).toBe(false);
  });

  it('has publish privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'publish')).toBe(true);
  });

  it('has no delete privilege on card', () => {
    expect(isOptionPresent(analysisOptions, 'delete')).toBe(false);
    browser.sleep(1000);
  });

  it('should close the menu', () => {
    // close the opened actions menu
    element(by.css('md-backdrop')).click();
    expect(element(by.css('md-backdrop')).isPresent()).toBe(false);
  });

  it('go to executed analysis page', () => {
    analyze.main.firstCardTitle.click();
    const condition = ec.urlContains('/executed');
    browser
      .wait(() => condition, 1000)
      .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));
  });

  it('should have publish privilege', () => {
    expect(executedAnalysis.publishBtn.isDisplayed()).toBe(true);
  });

  it('should have fork privilege', () => {
    expect(executedAnalysis.forkBtn.isDisplayed()).toBe(false);
  });

  it('should have edit privilege', () => {
    expect(executedAnalysis.editBtn.isDisplayed()).toBe(false);
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
