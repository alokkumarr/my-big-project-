const login = require('../javascript/pages/common/login.po.js');
const sidenav = require('../javascript/pages/components/sidenav.co.js');
const analyze = require('../javascript/pages/common/analyze.po.js');
const protractor = require('protractor');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const {hasClass} = require('../javascript/helpers/utils');

describe('create a new pivot type analysis', () => {
  let categoryName;
  const pivotDesigner = analyze.designerDialog.pivot;
  const pivotName = `e2e pivot${(new Date()).toString()}`;
  const pivotDescription = 'e2e pivot description';
  const dataField = 'Available MB';
  const filterValue = 'SAMSUNG';
  const columnField = 'Source Manufacturer';
  const rowField = 'Source OS';
  const metric = 'MCT Content';
  const method = 'table:pivot';

  it('login as admin', () => {
    browser.waitForAngular();
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

  it('should select pivot type and proceed', () => {
    const newDialog = analyze.newDialog;
    newDialog.getMetric(metric).click();
    newDialog.getMethod(method).click();
    newDialog.createBtn.click();
    expect(pivotDesigner.title.isPresent()).toBe(true);
  });

  it('should apply filters', () => {
    const filters = analyze.filtersDialog;
    const filterAC = filters.getFilterAutocomplete(0);
    const stringFilterInput = filters.getStringFilterInput(0);
    const fieldName = columnField;

    pivotDesigner.openFiltersBtn.click();
    filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
    stringFilterInput.sendKeys(filterValue, protractor.Key.TAB);
    filters.applyBtn.click();
    const filterName = filters.getAppliedFilter(fieldName);

    commonFunctions.waitFor.elementToBePresent(filterName);
    expect(filterName.isPresent()).toBe(true);
  });

  it('should select row, column and data fields and refresh data', () => {
    const refreshBtn = pivotDesigner.refreshBtn;

    pivotDesigner.getPivotFieldCheckbox(dataField).click();
    pivotDesigner.doSelectPivotArea(dataField, 'data');
    pivotDesigner.doSelectPivotAggregate(dataField, 'sum');

    pivotDesigner.getPivotFieldCheckbox(columnField).click();
    pivotDesigner.doSelectPivotArea(columnField, 'column');

    pivotDesigner.getPivotFieldCheckbox(rowField).click();
    pivotDesigner.doSelectPivotArea(rowField, 'row');

    const doesDataNeedRefreshing = hasClass(refreshBtn, 'btn-primary');
    expect(doesDataNeedRefreshing).toBeTruthy();
    refreshBtn.click();
  });

  it('should attempt to save the report', () => {
    const save = analyze.saveDialog;
    const designer = analyze.designerDialog;
    commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
    designer.saveBtn.click();

    expect(designer.saveDialog).toBeTruthy();
    expect(save.selectedCategory.getText()).toEqual(categoryName);

    save.nameInput.clear().sendKeys(pivotName);
    save.descriptionInput.clear().sendKeys(pivotDescription);
    save.saveBtn.click();
    commonFunctions.waitFor.elementToBePresent(analyze.main.getCardTitle(pivotName))
      .then(() => expect(analyze.main.getCardTitle(pivotName).isPresent()).toBe(true));
  });

  it('should delete the created analysis', () => {
    const main = analyze.main;
    main.getAnalysisCards(pivotName).count()
      .then(count => {
        main.doAnalysisAction(pivotName, 'delete');
        main.confirmDeleteBtn.click();
        expect(main.getAnalysisCards(pivotName).count()).toBe(count - 1);
      });
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
