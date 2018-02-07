/*
  Created by Alex
 */

const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const homePage = require('../javascript/pages/homePage.po.js');
const executedAnalysis = require('../javascript/pages/savedAlaysisPage.po');
const protractor = require('protractor');
const ec = protractor.ExpectedConditions;
const commonFunctions = require('../javascript/helpers/commonFunctions');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../conf/protractor.conf');

//TODO add case for No Privileges
//TODO add case for changing privileges
//TODO add case for changing multiple privileges
//TODO add case for making privilege inactive
//TODO add case for making multiple privileges inactive
//TODO add case for adding new privilege
describe('Privileges tests: privileges.test.js', () => {
  const dataProvider =  {
    //TODO change user hardcode name to users object
    'All privileges for user': {
      user: 'userOne',
      subCategory: "All DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: true,
      edit: true,
      fork: true,
      publish: true,
      execute: true,
      export: true,
      delete: true
    },
    'Create privilege for user': {
      user: 'userOne',
      subCategory: "Create DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: true,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Edit privilege for user': {
      user: 'userOne',
      subCategory: "Edit DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: false,
      edit: true,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Fork privilege for user': {
      user: 'userOne',
      subCategory: "Fork DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: false,
      edit: false,
      fork: true,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Publish privilege for user': {
      user: 'userOne',
      subCategory: "Publish DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: false,
      edit: false,
      fork: false,
      publish: true,
      execute: false,
      export: false,
      delete: false
    },
    'Execute privilege for user': {
      user: 'userOne',
      subCategory: "Execute DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: true,
      export: false,
      delete: false
    },
    'Export privilege for user': {
      user: 'userOne',
      subCategory: "Export DO NOT TOUCH",
      cardOptions: false,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: true,
      delete: false
    },
    'Delete privilege for user': {
      user: 'userOne',
      subCategory: "Delete DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: true
    },
    'View privilege for user': {
      user: 'userOne',
      subCategory: "View DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Multiple privileges for user': {
      user: 'userOne',
      subCategory: "Multiple DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: true,
      edit: false,
      fork: true,
      publish: true,
      execute: false,
      export: false,
      delete: false
    },
    'All privileges for admin': {
      user: 'admin',
      subCategory: "All DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: true,
      edit: true,
      fork: true,
      publish: true,
      execute: true,
      export: true,
      delete: true
    },
    'Create privilege for admin': {
      user: 'admin',
      subCategory: "Create DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: true,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Edit privilege for admin': {
      user: 'admin',
      subCategory: "Edit DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: false,
      edit: true,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Fork privilege for admin': {
      user: 'admin',
      subCategory: "Fork DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: false,
      edit: false,
      fork: true,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Publish privilege for admin': {
      user: 'admin',
      subCategory: "Publish DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: false,
      edit: false,
      fork: false,
      publish: true,
      execute: false,
      export: false,
      delete: false
    },
    'Execute privilege for admin': {
      user: 'admin',
      subCategory: "Execute DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: true,
      export: false,
      delete: false
    },
    'Export privilege for admin': {
      user: 'admin',
      subCategory: "Export DO NOT TOUCH",
      cardOptions: false,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: true,
      delete: false
    },
    'Delete privilege for admin': {
      user: 'admin',
      subCategory: "Delete DO NOT TOUCH",
      cardOptions: true,
      viewOptions: true,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: true
    },
    'View privilege for admin': {
      user: 'admin',
      subCategory: "View DO NOT TOUCH",
      cardOptions: false,
      viewOptions: false,
      create: false,
      edit: false,
      fork: false,
      publish: false,
      execute: false,
      export: false,
      delete: false
    },
    'Multiple privileges for admin': {
      user: 'admin',
      subCategory: "Multiple DO NOT TOUCH",
      cardOptions: true,
      viewOptions: false,
      create: true,
      edit: false,
      fork: true,
      publish: true,
      execute: false,
      export: false,
      delete: false
    },
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.fluentWait)
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.fluentWait)
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(dataProvider, function (data, description) {
    it('should check ' + description, () => {
        loginPage.loginAs(data.user);
        navigateToDefaultSubCategory();

        // Validate presence of Create Button
        element(analyzePage.analysisElems.addAnalysisBtn.isDisplayed().then(function (isVisible) {
          expect(isVisible).toBe(data.create,
            "Create button expected to be " + data.create + " on Analyze Page, but was " + !data.create);
        }));

        // Go to Card View
        commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

        // Validate presence of menu on card
        element(analyzePage.analysisElems.cardMenuButton.isDisplayed().then(function (isVisible) {
          expect(isVisible).toBe(data.cardOptions,
            "Options on card expected to be " + data.cardOptions + " on Analyze Page, but was " + !data.cardOptions);
        }));

        // Validate presence on menu items in card menu
        if (data.cardOptions) {
          analyzePage.main.getAnalysisActionOptions(analyzePage.main.firstCard).then(options => {
            let analysisOptions = options;
            expect(options.isPresent()).toBe(true, "Options on card expected to be present on Analyze Page, but weren't");

            //should check privileges on card
            expect(isOptionPresent(analysisOptions, "edit")).toBe(data.edit,
              "Edit button expected to be " + data.edit + " on Analyze Page, but was " + !data.edit);
            expect(analyzePage.main.getForkBtn(analyzePage.main.firstCard).isDisplayed()).toBe(data.fork,
              "Fork button expected to be " + data.fork + " on Analyze Page, but was " + !data.fork);
            expect(isOptionPresent(analysisOptions, 'publish')).toBe(data.publish,
              "Publish button expected to be " + data.publish + " on Analyze Page, but was " + !data.publish);
            expect(isOptionPresent(analysisOptions, 'execute')).toBe(data.execute,
              "Execute button expected to be " + data.execute + " on Analyze Page, but was " + !data.execute);
            expect(isOptionPresent(analysisOptions, 'delete')).toBe(data.delete,
              "Delete button expected to be " + data.delete + " on Analyze Page, but was " + !data.delete);
          });

          // Navigate back, close the opened actions menu
          //browser.sleep(1000);
          element(by.css('md-backdrop')).click();
          expect(element(by.css('md-backdrop')).isPresent()).toBe(false);
        }

        // Go to executed analysis page
        analyzePage.main.firstCardTitle.click();
        const condition = ec.urlContains('/executed');
        browser
          .wait(() => condition, protractorConf.timeouts.fluentWait)
          .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));

        // Validate buttons in view mode of analysis
        expect(executedAnalysis.editBtn.isDisplayed()).toBe(data.edit,
          "Edit privilege expected to be " + data.edit + " in view mode, but was " + !data.edit);
        expect(executedAnalysis.forkBtn.isDisplayed()).toBe(data.fork,
          "Fork button expected to be " + data.fork + " in view mode, but was " + !data.fork);
        expect(executedAnalysis.publishBtn.isDisplayed()).toBe(data.publish,
          "Publish button expected to be " + data.publish + " in view mode, but was" + !data.publish);

        // Validate menu in analysis
        element(executedAnalysis.actionsMenuBtn.isDisplayed().then(function (isVisible) {
          expect(isVisible).toBe(data.viewOptions,
            "Options menu button expected to be " + data.viewOptions + " in view mode, but was " + !data.viewOptions);
        }));

        // Validate menu items under menu button
        if (data.viewOptions === true) {

          executedAnalysis.actionsMenuBtn.click();

          element(executedAnalysis.executeMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.execute,
              "Execute button expected to be " + data.execute + " in view mode, but was" + !data.execute);
          }));

          element(executedAnalysis.exportMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.export,
              "Export button expected to be " + data.export + " in view mode, but was" + !data.export);
          }));

          element(executedAnalysis.deleteMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.delete,
              "Delete button expected to be " + data.delete + " in view mode, but was" + !data.delete);
          }));
        }
      }
    );

    function isOptionPresent(options, optionName) {
      const option = analyzePage.main.getAnalysisOption(options, optionName);
      return option.isPresent();
    }

    // Navigates to specific category where analysis view should happen
    const navigateToDefaultSubCategory = () => {
      const subCategory = homePage.subCategory(data.subCategory);
      commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);
    };
  });
});
