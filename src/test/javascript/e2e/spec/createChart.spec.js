const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');
const protractor = require('protractor');

const ec = protractor.ExpectedConditions;

const navigateToAnalyze = () => {
  browser.driver.get('http://localhost:3000');
  // the app should automatically navigate to the analyze page
  // and when its on there th ecurrent module link is disabled
  const alreadyOnAnalyzePage = ec.urlContains('/analyze');

  // wait for the app to automatically navigate to the default page
  browser
    .wait(() => alreadyOnAnalyzePage, 1000)
    .then(() => expect(browser.getCurrentUrl()).toContain('/analyze'));
};

describe('create a new columnChart type analysis', () => {
  let categoryName;

  it('should automatically redirect to Analyze page when going to the homepage', navigateToAnalyze);

  it('should open the sidenav menu and go to first category', () => {
    sidenav.menuBtn.click();
    sidenav.publicCategoriesToggle.click();
    categoryName = sidenav.firstPublicCategory.getText();
    sidenav.firstPublicCategory.click();
    expect(analyze.main.categoryTitle.getText()).toEqual(categoryName);
  });

  it('should display card view by default', () => {
    analyze.validateCardView();
  });

  it('should open the new Analysis dialog', () => {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateNewAnalyze();
  });

  it('should select Column Chart type and proceed', () => {
    const newDialog = analyze.newDialog;
    const chartContainer = analyze.designerDialog.chart.container;
    newDialog.getMetric('MCT Content').click();
    newDialog.getMethod('chart:column').click();
    newDialog.createBtn.click();
    expect(chartContainer.isDisplayed()).toBeTruthy();
  });

  // it('should display the added fields to chart', () => {
  // });

  // it('should apply filters', () => {
  // });

  const chartName = 'e2e column chart';
  const chartDescription = 'e2e test chart description';

  it('should attempt to save the report', () => {
    const save = analyze.saveDialog;
    const designer = analyze.designerDialog;
    designer.saveBtn.click();

    expect(designer.elem).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName);

    save.nameInput.clear().sendKeys(chartName);
    save.descriptionInput.clear().sendKeys(chartDescription);
    save.saveBtn.click();
    browser.sleep(4000);

    const newAnalysis = analyze.main.getCardTitle(chartName);
    expect(newAnalysis.isDisplayed()).toBeTruthy();
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    main.getAnalysisCards(chartName).count()
      .then(count => {
        main.doAnalysisAction(chartName, 'delete');
        main.confirmDeleteBtn.click();
        browser.sleep(4000);
        expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
      });
  });
});
