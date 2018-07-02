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
const protractorConf = require('../../../../saw-web/conf/protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
const utils = require('../javascript/helpers/utils');

//TODO add case for No Privileges
//TODO add case for changing privileges
//TODO add case for changing multiple privileges
//TODO add case for making privilege inactive
//TODO add case for making multiple privileges inactive
//TODO add case for adding new privilege
describe('Privileges tests: privileges.test.js', () => {
  const categoryName = categories.privileges.name;

  const dataProvider = {
    //TODO change user hardcode name to users object
    'All privileges for user': { // SAWQA-4834
      user: 'userOne',
      subCategory: subCategories.all.name,
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
    'Create privilege for user': { // SAWQA-4835
      user: 'userOne',
      subCategory: subCategories.create.name,
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
    'Edit privilege for user': {  // SAWQA-4836
      user: 'userOne',
      subCategory: subCategories.edit.name,
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
    'Fork privilege for user': { // SAWQA-4837
      user: 'userOne',
      subCategory: subCategories.fork.name,
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
    'Publish privilege for user': { // SAWQA-4838
      user: 'userOne',
      subCategory: subCategories.publish.name,
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
    'Execute privilege for user': { // SAWQA-4839
      user: 'userOne',
      subCategory: subCategories.execute.name,
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
    'Export privilege for user': { // SAWQA-4840
      user: 'userOne',
      subCategory: subCategories.export.name,
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
    'Delete privilege for user': { // SAWQA-4841
      user: 'userOne',
      subCategory: subCategories.delete.name,
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
    'View privilege for user': { // SAWQA-4842
      user: 'userOne',
      subCategory: subCategories.view.name,
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
    'Multiple privileges for user': { // SAWQA-4843
      user: 'userOne',
      subCategory: subCategories.multiple.name,
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
    'All privileges for admin': { // SAWQA-4844
      user: 'admin',
      subCategory: subCategories.all.name,
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
    // 'Create privilege for admin': { // SAWQA-4845
    //   user: 'admin',
    //   subCategory: subCategories.create.name,
    //   cardOptions: false,
    //   viewOptions: false,
    //   create: true,
    //   edit: false,
    //   fork: false,
    //   publish: false,
    //   execute: false,
    //   export: false,
    //   delete: false
    // },
    // 'Edit privilege for admin': {  // SAWQA-4846
    //   user: 'admin',
    //   subCategory: subCategories.edit.name,
    //   cardOptions: true,
    //   viewOptions: false,
    //   create: false,
    //   edit: true,
    //   fork: false,
    //   publish: false,
    //   execute: false,
    //   export: false,
    //   delete: false
    // },
    // 'Fork privilege for admin': {  // SAWQA-4847
    //   user: 'admin',
    //   subCategory: subCategories.fork.name,
    //   cardOptions: false,
    //   viewOptions: false,
    //   create: false,
    //   edit: false,
    //   fork: true,
    //   publish: false,
    //   execute: false,
    //   export: false,
    //   delete: false
    // },
    // 'Publish privilege for admin': { // SAWQA-4848
    //   user: 'admin',
    //   subCategory: subCategories.publish.name,
    //   cardOptions: true,
    //   viewOptions: false,
    //   create: false,
    //   edit: false,
    //   fork: false,
    //   publish: true,
    //   execute: false,
    //   export: false,
    //   delete: false
    // },
    // 'Execute privilege for admin': { // SAWQA-4849
    //   user: 'admin',
    //   subCategory: subCategories.execute.name,
    //   cardOptions: true,
    //   viewOptions: true,
    //   create: false,
    //   edit: false,
    //   fork: false,
    //   publish: false,
    //   execute: true,
    //   export: false,
    //   delete: false
    // },
    // 'Export privilege for admin': { // SAWQA-4850
    //   user: 'admin',
    //   subCategory: subCategories.export.name,
    //   cardOptions: false,
    //   viewOptions: true,
    //   create: false,
    //   edit: false,
    //   fork: false,
    //   publish: false,
    //   execute: false,
    //   export: true,
    //   delete: false
    // },
    // 'Delete privilege for admin': { // SAWQA-4851
    //   user: 'admin',
    //   subCategory: subCategories.delete.name,
    //   cardOptions: true,
    //   viewOptions: true,
    //   create: false,
    //   edit: false,
    //   fork: false,
    //   publish: false,
    //   execute: false,
    //   export: false,
    //   delete: true
    // },
    // 'View privilege for admin': { // SAWQA-4852
    //   user: 'admin',
    //   subCategory: subCategories.view.name,
    //   cardOptions: false,
    //   viewOptions: false,
    //   create: false,
    //   edit: false,
    //   fork: false,
    //   publish: false,
    //   execute: false,
    //   export: false,
    //   delete: false
    // },
    // 'Multiple privileges for admin': { // SAWQA-4853
    //   user: 'admin',
    //   subCategory: subCategories.multiple.name,
    //   cardOptions: true,
    //   viewOptions: false,
    //   create: true,
    //   edit: false,
    //   fork: true,
    //   publish: true,
    //   execute: false,
    //   export: false,
    //   delete: false
    // }
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    commonFunctions.logOutByClearingLocalStorage();
  });

  using(dataProvider, function (data, description) {
    it('should check ' + description, () => {
      
        loginPage.loginAs(data.user);
        navigateToDefaultSubCategory();

        // Validate presence of Create Button
        element(analyzePage.analysisElems.addAnalysisBtn.isPresent().then(function (isVisible) {
          expect(isVisible).toBe(data.create,
            "Create button expected to be " + data.create + " on Analyze Page, but was " + !data.create);
        }));

        // Go to Card View
        commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
        analyzePage.analysisElems.cardView.click();

        element(analyzePage.analysisElems.cardMenuButton.isPresent().then(function (isVisible) {
          expect(isVisible).toBe(data.cardOptions,
            "Options on card expected to be " + data.cardOptions + " on Analyze Page, but was " + !data.cardOptions);
        }));
        // Validate presence on menu items in card menu
        if (data.cardOptions) {
          analyzePage.main.getAnalysisActionOptionsNew(analyzePage.main.firstCard).then(options => {
            let analysisOptions = options;
            expect(options.isPresent()).toBe(true, "Options on card expected to be present on Analyze Page, but weren't");
            //should check privileges on card
            expect(isOptionPresent(analysisOptions, "edit")).toBe(data.edit,
              "Edit button expected to be " + data.edit + " on Analyze Page, but was " + !data.edit);
            expect(analyzePage.main.getForkBtn(analyzePage.main.firstCard).isPresent()).toBe(data.fork,
              "Fork button expected to be " + data.fork + " on Analyze Page, but was " + !data.fork);
            expect(isOptionPresent(analysisOptions, 'publish')).toBe(data.publish,
              "Publish button expected to be " + data.publish + " on Analyze Page, but was " + !data.publish);
            expect(isOptionPresent(analysisOptions, 'execute')).toBe(data.execute,
              "Execute button expected to be " + data.execute + " on Analyze Page, but was " + !data.execute);
            expect(isOptionPresent(analysisOptions, 'delete')).toBe(data.delete,
              "Delete button expected to be " + data.delete + " on Analyze Page, but was " + !data.delete);
          });

          // Navigate back, close the opened actions menu
          commonFunctions.waitFor.elementToBeClickable(element(by.css('[class="cdk-overlay-container"]')));
          element(by.css('[class="cdk-overlay-container"]')).click();
          commonFunctions.waitFor.elementToBeNotVisible(analyzePage.main.actionMenuOptions);
          expect(analyzePage.main.actionMenuOptions.isPresent()).toBe(false);
        }
        // Go to executed analysis page
        commonFunctions.waitFor.elementToBeClickable(analyzePage.main.firstCardTitle);
        analyzePage.main.firstCardTitle.click();

        const condition = ec.urlContains('/executed');
        browser
          .wait(() => condition, protractorConf.timeouts.pageResolveTimeout)
          .then(() => expect(browser.getCurrentUrl()).toContain('/executed'));

        // Validate buttons in view mode of analysis
        expect(executedAnalysis.editBtn.isDisplayed()).toBe(data.edit,
          "Edit privilege expected to be " + data.edit + " in view mode, but was " + !data.edit);
        expect(executedAnalysis.forkBtn.isDisplayed()).toBe(data.fork,
          "Fork button expected to be " + data.fork + " in view mode, but was " + !data.fork);
        expect(executedAnalysis.publishBtn.isDisplayed()).toBe(data.publish,
          "Publish button expected to be " + data.publish + " in view mode, but was " + !data.publish);

        // Validate menu in analysis
        element(executedAnalysis.actionsMenuBtn.isDisplayed().then(function (isVisible) {
          expect(isVisible).toBe(data.viewOptions,
            "Options menu button expected to be " + data.viewOptions + " in view mode, but was " + !data.viewOptions);
        }));

        // Validate menu items under menu button
        if (data.viewOptions === true) {

          commonFunctions.waitFor.elementToBeClickable(executedAnalysis.actionsMenuBtn);
          executedAnalysis.actionsMenuBtn.click();

          element(executedAnalysis.executeMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.execute,
              "Execute button expected to be " + data.execute + " in view mode, but was " + !data.execute);
          }));

          element(executedAnalysis.exportMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.export,
              "Export button expected to be " + data.export + " in view mode, but was " + !data.export);
          }));

          element(executedAnalysis.deleteMenuOption.isPresent().then(function (isVisible) {
            expect(isVisible).toBe(data.delete,
              "Delete button expected to be " + data.delete + " in view mode, but was " + !data.delete);
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
      homePage.mainMenuExpandBtn.click();
      commonFunctions.waitFor.elementToBeVisible(homePage.collapsedCategoryUpdated(categoryName));
      commonFunctions.waitFor.elementToBeClickable(homePage.collapsedCategoryUpdated(categoryName));
      homePage.collapsedCategoryUpdated(categoryName).click();
      browser.sleep(500);
      commonFunctions.waitFor.elementToBeVisible(homePage.subCategory(data.subCategory));
      commonFunctions.waitFor.elementToBeClickable(homePage.subCategory(data.subCategory));
      homePage.subCategory(data.subCategory).click();
      browser.sleep(500);
      const doesDataNeedRefreshing = utils.hasClass(homePage.subCategory(data.subCategory), 'activeButton');
      expect(doesDataNeedRefreshing).toBeTruthy();
      homePage.mainMenuCollapseBtn.click();

    };
  });
});
