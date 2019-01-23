const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories').subCategories;
const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const Header = require('../../pages/components/Header');

describe('Executing privilege tests from privilege.test.js', () => {
  const categoryName = categories.privileges.name;

  beforeAll(() => {
    logger.info('Starting privilege tests...');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach((done) => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach((done) => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['PRIVILEGES']['positiveTests'] ? testDataReader.testData['PRIVILEGES']['positiveTests'] : {}, (data, id) => {
    it(`${id}:${data.description}`, () => {
      logger.info('Executing test case ID:' + id);
      const loginPage = new LoginPage();
      loginPage.loginAs(data.user, /analyze/);

      const header = new Header();
      header.verifyLogo();
      header.openCategoryMenu();
      header.selectCategory(categoryName);
      header.selectSubCategory(subCategories[data.subCategory].name);

      const analyzePage = new AnalyzePage();
      analyzePage.verifyElementPresent(analyzePage._addAnalysisButton, data.create, "Add Analysis button on card is expected to be " + data.create + " on Analyze Page, but was " + !data.create);

      analyzePage.goToView('card');

      analyzePage.verifyElementPresent(analyzePage._actionMenuButton, data.cardOptions, "Action menu button on card is expected to be " + data.cardOptions + " on Analyze Page, but was " + !data.cardOptions);

      analyzePage.verifyElementPresent(analyzePage._forkButton, data.fork, "Fork button on card is expected to be " + data.fork + " on Analyze Page, but was " + !data.fork);

      analyzePage.clickOnActionMenu();

      analyzePage.verifyElementPresent(analyzePage._executeButton, data.execute, "Execute button on card is expected to be " + data.execute + " on Analyze Page, but was " + !data.execute);

      analyzePage.verifyElementPresent(analyzePage._editButton, data.edit, "Edit button on card is expected to be " + data.edit + " on Analyze Page, but was " + !data.edit);

      analyzePage.verifyElementPresent(analyzePage._publishButton, data.publish, "Publish button on card is expected to be " + data.publish + " on Analyze Page, but was " + !data.publish);

      analyzePage.verifyElementPresent(analyzePage._scheduleButton, data.schedule,
        "Schedule button on card is expected to be " + data.schedule + " on Analyze Page, but was " + !data.schedule);

      analyzePage.verifyElementPresent(analyzePage._deleteButton, data.delete,
        "Delete button on card is expected to be " + data.delete + " on Analyze Page, but was " + !data.delete);

      // Navigate back, close the opened actions menu
      analyzePage.closeOpenedActionMenuFromCardView();
      analyzePage.gotoAnalysisExecutePageFromCardView();
      // Validate buttons in view mode of analysis
      analyzePage.verifyElementPresent(analyzePage._editButtonOnExcutePage, data.edit, "Edit button on execute page is expected to be " + data.edit + " but was " + !data._editButtonOnExcutePage);

      analyzePage.verifyElementPresent(analyzePage._forkAndEditButton, data.fork,
        "Fork button on execute page is expected to be " + data.fork + " but was " + !data.fork);

      analyzePage.verifyElementPresent(analyzePage._actionMenuButton, data.viewOptions,
        "Action button on execute page is expected to be " + data.viewOptions + " but was " + !data.viewOptions);

      analyzePage.clickOnActionMenu();

      analyzePage.verifyElementPresent(analyzePage._executeButton, data.execute,
        "Execute action button on execute page is expected to be " + data.execute + " but was " + !data.execute);

      analyzePage.verifyElementPresent(analyzePage._publishButton, data.publish, "Publish action button on execute page is expected to be " + data.publish + " but was " + !data.publish);

      analyzePage.verifyElementPresent(analyzePage._scheduleButton, data.schedule,
        "Schedule action button on execute page is expected to be " + data.schedule + " but was " + !data.schedule);

      analyzePage.verifyElementPresent(analyzePage._actionExportButton, data.export,
        "Export action button on execute page is expected to be " + data.export + " but was " + !data.export);

      analyzePage.verifyElementPresent(analyzePage._deleteButton, data.delete, "Delete action button on execute page is expected to be " + data.delete + " but was " + !data.delete);

      analyzePage.verifyElementPresent(analyzePage._actionDetailsButton, true, "Detail action button on execute page is expected to be " + true + " but was " + false);

    }).result.testInfo = { testId: id, data: data, feature: 'PRIVILEGES', dataProvider: 'positiveTests' };
  });
});
