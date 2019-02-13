const login = require('../pages/loginPage.po.js');
const analyzePage = require('../pages/analyzePage.po.js');
const commonFunctions = require('./commonFunctions.js');
const homePage = require('../pages/homePage.po');
const savedAlaysisPage = require('../pages/savedAlaysisPage.po');
const designModePage = require('../pages/designModePage.po.js');
const Constants = require('../api/Constants');
const utils = require('./utils');
const commonElementsPage = require('../pages/commonElementsPage.po');
let analysisId;

class PromptFilterFunctions {
  verifyPromptFromListView(
    categoryName,
    subCategoryName,
    defaultCategory,
    name,
    data,
    execute
  ) {
    try {
      let _self = this;
      //  From analysis listview page
      browser.ignoreSynchronization = false;
      analyzePage.navigateToHome();
      browser.ignoreSynchronization = true;
      homePage.navigateToSubCategoryUpdated(
        categoryName,
        subCategoryName,
        defaultCategory
      );
      //  Change to list View.
      element(
        utils
          .hasClass(homePage.listViewInput, 'mat-radio-checked')
          .then(function(isPresent) {
            if (!isPresent) {
              commonFunctions.waitFor.elementToBeVisible(
                analyzePage.analysisElems.listView
              );
              commonFunctions.waitFor.elementToBeClickable(
                analyzePage.analysisElems.listView
              );
              analyzePage.analysisElems.listView.click();
            }
          })
      );

      if (execute) {
        commonFunctions.waitFor.elementToBeVisible(
          savedAlaysisPage.analysisAction(name)
        );
        commonFunctions.waitFor.elementToBeClickable(
          savedAlaysisPage.analysisAction(name)
        );
        savedAlaysisPage.analysisAction(name).click();
        commonFunctions.waitFor.elementToBeVisible(
          savedAlaysisPage.executeMenuOption
        );
        commonFunctions.waitFor.elementToBeClickable(
          savedAlaysisPage.executeMenuOption
        );
        savedAlaysisPage.executeMenuOption.click();
      } else {
        //  Open the created analysis.
        const analysisName = analyzePage.listViewItem(name);
        commonFunctions.waitFor.elementToBeVisible(analysisName);
        commonFunctions.waitFor.elementToBeClickable(analysisName);
        analysisName.click();
      }
      _self.verifyFilters(data);
    } catch (e) {
      console.log(e);
    }
  }

  verifyPromptFromCardView(
    categoryName,
    subCategoryName,
    defaultCategory,
    name,
    data,
    execute
  ) {
    try {
      let _self = this;
      //  From analysis card page
      browser.ignoreSynchronization = false;
      analyzePage.navigateToHome();
      browser.ignoreSynchronization = true;
      homePage.navigateToSubCategoryUpdated(
        categoryName,
        subCategoryName,
        defaultCategory
      );
      //  Change to Card View.
      element(
        utils
          .hasClass(homePage.cardViewInput, 'mat-radio-checked')
          .then(function(isPresent) {
            if (isPresent) {
              commonFunctions.waitFor.elementToBeVisible(
                analyzePage.analysisElems.cardView
              );
              commonFunctions.waitFor.elementToBeClickable(
                analyzePage.analysisElems.cardView
              );
              analyzePage.analysisElems.cardView.click();
            }
          })
      );

      if (execute) {
        commonFunctions.waitFor.elementToBeVisible(
          savedAlaysisPage.analysisAction(name)
        );
        commonFunctions.waitFor.elementToBeClickable(
          savedAlaysisPage.analysisAction(name)
        );
        savedAlaysisPage.analysisAction(name).click();
        commonFunctions.waitFor.elementToBeVisible(
          savedAlaysisPage.executeMenuOption
        );
        commonFunctions.waitFor.elementToBeClickable(
          savedAlaysisPage.executeMenuOption
        );
        savedAlaysisPage.executeMenuOption.click();
      } else {
        //  Open the created analysis.
        const analysisName = analyzePage.main.getCardTitle(name);
        commonFunctions.waitFor.elementToBeVisible(analysisName);
        commonFunctions.waitFor.elementToBeClickable(analysisName);
        analysisName.click();
      }
      _self.verifyFilters(data);
    } catch (e) {
      console.log(e);
    }
  }

  verifyPromptFromDetailPage(
    categoryName,
    subCategoryName,
    defaultCategory,
    name,
    data,
    analysisType = null
  ) {
    try {
      let _self = this;
      //  Execute the analysis from detail/view page and verify it asks for prompt filter
      browser.waitForAngular();
      browser.sleep(5000); // This is required
      //  From analysis card page
      browser.ignoreSynchronization = false;
      analyzePage.navigateToHome();
      browser.ignoreSynchronization = true;
      homePage.navigateToSubCategoryUpdated(
        categoryName,
        subCategoryName,
        defaultCategory
      );
      element(
        utils
          .hasClass(homePage.cardViewInput, 'mat-radio-checked')
          .then(function(isPresent) {
            if (!isPresent) {
              commonFunctions.waitFor.elementToBeVisible(
                analyzePage.analysisElems.cardView
              );
              commonFunctions.waitFor.elementToBeClickable(
                analyzePage.analysisElems.cardView
              );
              analyzePage.analysisElems.cardView.click();
            }
            // Open the created analysis.
            const analysisName = analyzePage.main.getCardTitle(name);
            commonFunctions.waitFor.elementToBeVisible(analysisName);
            commonFunctions.waitFor.elementToBeClickable(analysisName);
            analysisName.click();
            browser.sleep(2000);

            if (analysisType !== Constants.REPORT) {
              commonFunctions.waitFor.elementToBeVisible(
                analyzePage.prompt.filterDialog
              );
              expect(
                analyzePage.prompt.filterDialog.isDisplayed()
              ).toBeTruthy();
              commonFunctions.waitFor.elementToBeVisible(
                analyzePage.prompt.cancleFilterPrompt
              );
              commonFunctions.waitFor.elementToBeClickable(
                analyzePage.prompt.cancleFilterPrompt
              );

              analyzePage.prompt.cancleFilterPrompt.click();
            }

            commonFunctions.waitFor.elementToBeVisible(
              savedAlaysisPage.actionsMenuBtn
            );
            commonFunctions.waitFor.elementToBeClickable(
              savedAlaysisPage.actionsMenuBtn
            );
            savedAlaysisPage.actionsMenuBtn.click();

            commonFunctions.waitFor.elementToBeVisible(
              savedAlaysisPage.executeMenuOption
            );
            commonFunctions.waitFor.elementToBeClickable(
              savedAlaysisPage.executeMenuOption
            );
            savedAlaysisPage.executeMenuOption.click();

            _self.verifyFilters(data);
          })
      );
    } catch (e) {
      console.log(e);
    }
  }

  verifyFilters(data) {
    try {
      let _self = this;
      commonFunctions.waitFor.elementToBeVisible(
        analyzePage.prompt.filterDialog
      );
      expect(analyzePage.prompt.filterDialog.isDisplayed()).toBeTruthy();
      expect(analyzePage.prompt.selectedField.getAttribute('value')).toEqual(
        data.fieldName
      );
      // apply filters and execute
      _self.setFilterValue(data.fieldType, data.operator, data.value);
    } catch (e) {
      console.log(e);
    }
  }

  setFilterValue(fieldType, operator, value1) {
    try {
      //  Scenario for dates
      const filterWindow = designModePage.filterWindow;
      if (fieldType === 'date') {
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.date.presetDropDown
        );
        filterWindow.date.presetDropDown.click();
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.date.presetDropDownItem(value1)
        );
        filterWindow.date.presetDropDownItem(value1).click();
      }

      //  Scenario for numbers
      if (fieldType === 'number') {
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.number.operator
        );
        filterWindow.number.operator.click();
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.number.operatorDropDownItem(operator)
        );
        filterWindow.number.operatorDropDownItem(operator).click();
        commonFunctions.waitFor.elementToBeVisible(filterWindow.number.input);
        filterWindow.number.input.click();
        filterWindow.number.input.clear().sendKeys(value1);
      }

      //  Scenario for strings
      if (fieldType === 'string') {
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.string.operator
        );
        filterWindow.string.operator.click();
        commonFunctions.waitFor.elementToBeClickable(
          filterWindow.string.operatorDropDownItem(operator)
        );
        filterWindow.string.operatorDropDownItem(operator).click();
        //  Select different input for Is in and Is not in operator TODO: we should be consistent
        if (operator === 'Is in' || operator === 'Is not in') {
          commonFunctions.waitFor.elementToBeVisible(
            filterWindow.string.isInIsNotInInput
          );
          filterWindow.string.isInIsNotInInput.clear().sendKeys(value1);
        } else {
          commonFunctions.waitFor.elementToBeVisible(filterWindow.string.input);
          filterWindow.string.input.clear().sendKeys(value1);
        }
      }
      commonFunctions.waitFor.elementToBeClickable(
        designModePage.applyFiltersBtn
      );
      designModePage.applyFiltersBtn.click();
      commonElementsPage.ifErrorPrintTextAndFailTest();
    } catch (e) {
      console.log(e);
    }
  }

  validateSelectedFilters(filters) {
    try {
      analyzePage.appliedFiltersDetails.selectedFilters
        .map(function(elm) {
          return elm.getText();
        })
        .then(function(displayedFilters) {
          expect(
            utils.arrayContainsArray(displayedFilters, filters)
          ).toBeTruthy();
        });
    } catch (e) {
      console.log(e);
    }
  }

  applyFilters(
    categoryName,
    subCategoryName,
    defaultCategory,
    user,
    name,
    description,
    analysisType,
    subType,
    fieldName
  ) {
    try {
      let _self = this;
      login.loginAs(user);
      homePage.navigateToSubCategoryUpdated(
        categoryName,
        subCategoryName,
        defaultCategory
      );
      // Change to Card View.
      commonFunctions.waitFor.elementToBeVisible(
        analyzePage.analysisElems.cardView
      );
      commonFunctions.waitFor.elementToBeClickable(
        analyzePage.analysisElems.cardView
      );
      analyzePage.analysisElems.cardView.click();
      // Open the created analysis.
      const createdAnalysis = analyzePage.main.getCardTitle(name);
      commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
      commonFunctions.waitFor.elementToBeClickable(createdAnalysis);
      createdAnalysis.click();
      // get analysis id from current url
      browser.getCurrentUrl().then(url => {
        analysisId = commonFunctions.getAnalysisIdFromUrl(url);
      });
      commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.editBtn);
      savedAlaysisPage.editBtn.click();
      // apply filters
      const filters = analyzePage.filtersDialogUpgraded;
      const filterAC = filters.getFilterAutocomplete(0);

      const chartDesigner = analyzePage.designerDialog.chart;
      commonFunctions.waitFor.elementToBeClickable(chartDesigner.filterBtn);
      chartDesigner.filterBtn.click();
      if (analysisType === Constants.REPORT) {
        commonFunctions.waitFor.elementToBeClickable(
          designModePage.filterWindow.addFilter('SALES')
        );
        designModePage.filterWindow.addFilter('SALES').click();
      } else {
        commonFunctions.waitFor.elementToBeClickable(
          designModePage.filterWindow.addFilter('sample')
        );
        designModePage.filterWindow.addFilter('sample').click();
      }
      filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
      commonFunctions.waitFor.elementToBeVisible(filters.prompt);
      commonFunctions.waitFor.elementToBeClickable(filters.prompt);
      filters.prompt.click();
      commonFunctions.waitFor.elementToBeClickable(filters.applyBtn);
      filters.applyBtn.click();
      browser.sleep(1000);
      // TODO: Need to check that filters applied or not.
      commonFunctions.waitFor.elementToBeVisible(
        analyzePage.appliedFiltersDetails.filterText
      );
      commonFunctions.waitFor.elementToBeVisible(
        analyzePage.appliedFiltersDetails.filterClear
      );
      commonFunctions.waitFor.elementToBeVisible(
        analyzePage.appliedFiltersDetails.selectedFiltersText
      );
      _self.validateSelectedFilters([fieldName]);
      // Save
      const save = analyzePage.saveDialog;
      const designer = analyzePage.designerDialog;
      commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
      designer.saveBtn.click();
      commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
      commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
      save.saveBtn.click();
    } catch (e) {
      console.log(e);
    }
  }
}

module.exports = PromptFilterFunctions;
