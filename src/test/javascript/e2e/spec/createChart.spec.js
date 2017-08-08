const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');
const protractor = require('protractor');
const {hasClass} = require('../utils');

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
  const chartDesigner = analyze.designerDialog.chart;
  const chartName = 'e2e column chart';
  const chartDescription = 'e2e test chart description';
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const filterValue = 'APPLE';
  const groupName = 'Source OS';
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
    const chartContainer = chartDesigner.container;
    newDialog.getMetric('MCT Content').click();
    newDialog.getMethod('chart:column').click();
    newDialog.createBtn.click();
    expect(chartContainer.isDisplayed()).toBeTruthy();
  });

  it('should apply filters', () => {
    const filters = analyze.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = xAxisName;

    chartDesigner.openFiltersBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    filters.applyBtn.click();

    expect(filters.getAppliedFilter(fieldName).isPresent()).toBe(true);
  });

  it('should select x, y axes and a grouping', () => {
    const refreshBtn = chartDesigner.refreshBtn;
    const x = chartDesigner.getXRadio(xAxisName);
    const y = chartDesigner.getYRadio(yAxisName);
    const g = chartDesigner.getGroupRadio(groupName);
    x.click();
    y.click();
    g.click();
    expect(hasClass(x, 'md-checked')).toBeTruthy();
    expect(hasClass(y, 'md-checked')).toBeTruthy();
    expect(hasClass(g, 'md-checked')).toBeTruthy();
    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    refreshBtn.click();
  });

  it('should attempt to save the report', () => {
    const save = analyze.saveDialog;
    const designer = analyze.designerDialog;
    designer.saveBtn.click();

    expect(designer.elem).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName);

    save.nameInput.clear().sendKeys(chartName);
    save.descriptionInput.clear().sendKeys(chartDescription);
    save.saveBtn.click();
    // const newAnalysis = analyze.main.getCardTitle(chartName);
    browser
    .wait(() => analyze.main.getCardTitle(chartName).isPresent(), 5000)
    .then(() => expect(analyze.main.getCardTitle(chartName).isPresent()).toBe(true));
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    main.getAnalysisCards(chartName).count()
      .then(count => {
        main.doAnalysisAction(chartName, 'delete');
        main.confirmDeleteBtn.click();
        expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
      });
  });
});
