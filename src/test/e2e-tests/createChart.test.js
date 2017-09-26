const login = require('../javascript/pages/common/login.po.js');
const sidenav = require('../javascript/pages/components/sidenav.co.js');
const analyze = require('../javascript/pages/common/analyze.po.js');
const protractor = require('protractor');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const {hasClass} = require('../javascript/helpers/utils');

describe('create columnChart type analysis', () => {
  let categoryName;
  const chartDesigner = analyze.designerDialog.chart;
  const chartName = `e2e column chart ${(new Date()).toString()}`;
  const chartDescription = 'e2e test chart description';
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const filterValue = 'APPLE';
  const groupName = 'Source OS';
  const metric = 'MCT Content';
  const method = 'chart:column';

  it('login as admin', () => {
    expect(browser.getCurrentUrl()).toContain('/login');
    login.loginAs('admin');
  });

  //Obsolete. Now menu opens automatically with first category expanded
  /* it('should open the sidenav menu and go to first category', () => {
    sidenav.menuBtn.click();
    sidenav.publicCategoriesToggle.click();
    categoryName = sidenav.firstPublicCategory.getText();
    sidenav.firstPublicCategory.click();
    expect(analyze.main.categoryTitle.getText()).toEqual(categoryName);
  }); */

  it('should display list view by default', () => {
    categoryName = sidenav.firstPublicCategory.getText();
    analyze.validateListView();
  });

  it('should switch to card view', () => {
    commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
    analyze.analysisElems.cardView.click();
  });

  it('should open the new Analysis dialog', () => {
    analyze.analysisElems.addAnalysisBtn.click();
    analyze.validateNewAnalyze();
  });

  it('should select Column Chart type and proceed', () => {
    const newDialog = analyze.newDialog;
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();
    expect(chartDesigner.title.isPresent()).toBe(true);
  });

  it('should select x, y axes and a grouping', () => {
    const refreshBtn = chartDesigner.refreshBtn;
    const x = chartDesigner.getXRadio(xAxisName);
    const y = chartDesigner.getYCheckBox(yAxisName);
    const g = chartDesigner.getGroupRadio(groupName);
    x.click();
    commonFunctions.waitFor.elementToBeClickable(y);
    y.click();
    g.click();
    const yParent = chartDesigner.getYCheckBoxParent(yAxisName);
    expect(hasClass(x, 'md-checked')).toBeTruthy();
    expect(hasClass(yParent, 'md-checked')).toBeTruthy();
    expect(hasClass(g, 'md-checked')).toBeTruthy();
    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    refreshBtn.click();
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

    const appliedFilter = filters.getAppliedFilter(fieldName);
    commonFunctions.waitFor.elementToBePresent(appliedFilter);
    expect(appliedFilter.isPresent()).toBe(true);
  });

  it('should attempt to save the report', () => {
    const save = analyze.saveDialog;
    const designer = analyze.designerDialog;
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    designer.saveBtn.click();

    commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
    expect(designer.saveDialog).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName); // TODO catch error here

    save.nameInput.clear().sendKeys(chartName);
    save.descriptionInput.clear().sendKeys(chartDescription);
    save.saveBtn.click();

    const createdAnalysis = analyze.main.getCardTitle(chartName);
    commonFunctions.waitFor.elementToBePresent(createdAnalysis)
      .then(() => expect(createdAnalysis.isPresent()).toBe(true));
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    const cards = main.getAnalysisCards(chartName);
    cards.count().then(count => {
      main.doAnalysisAction(chartName, 'delete');
      commonFunctions.waitFor.elementToBeClickable(main.confirmDeleteBtn);
      main.confirmDeleteBtn.click();

      commonFunctions.waitFor.cardsCountToUpdate(cards, count);

      expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
    });
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
