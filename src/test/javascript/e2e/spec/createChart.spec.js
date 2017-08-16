const login = require('../pages/common/login.po.js');
const sidenav = require('../pages/components/sidenav.co.js');
const analyze = require('../pages/common/analyze.po.js');
const protractor = require('protractor');
const commonFunctions = require('../helpers/commonFunctions.js');
const {hasClass} = require('../utils');

describe('create a new columnChart type analysis', () => {
  let categoryName;
  const chartDesigner = analyze.designerDialog.chart;
  const chartName = 'e2e column chart';
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
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();
    expect(chartDesigner.title.isPresent()).toBe(true);
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
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    designer.saveBtn.click();

    expect(designer.elem).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName);

    save.nameInput.clear().sendKeys(chartName);
    save.descriptionInput.clear().sendKeys(chartDescription);
    save.saveBtn.click();
    // const newAnalysis = analyze.main.getCardTitle(chartName);
    commonFunctions.waitFor.elementToBePresent(analyze.main.getCardTitle(chartName))
      .then(() => expect(analyze.main.getCardTitle(chartName).isPresent()).toBe(true));
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    main.getAnalysisCards(chartName).count()
      .then(count => {
        main.doAnalysisAction(chartName, 'delete');
        commonFunctions.waitFor.elementToBeClickable(main.confirmDeleteBtn);
        main.confirmDeleteBtn.click();
        expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
      });
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
