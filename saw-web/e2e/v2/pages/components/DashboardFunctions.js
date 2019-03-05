const HeaderPage = require('../../pages/components/Header');
const ObservePage = require('../../pages/ObservePage');
const AnalysisHelper = require('../../helpers/api/AnalysisHelper');


class DashboardFunctions{

    constructor(){

    }
    goToObserve() {
        let headerPage = new HeaderPage();
        headerPage.clickOnModuleLauncher();
        headerPage.clickOnObserveLink();       
    }

    addNewDashBoardFromExistingAnalysis(dashboardName,
        dashboardDescription,
        analysisCat,
        analysisSubCat,
        observeSubCat,
        analysesToAdd) 
        {
            let dashboardId;
            try{
                let _self = this;    
                let observePage = new ObservePage();
                observePage.clickOnAddDashboardButton();
                observePage.clickOnAddWidgetButton();
                observePage.clickOnExistingAnalysisLink();        

                _self.addAnalysesToDashboard(analysisCat, analysisSubCat, analysesToAdd, observePage);
                dashboardId = _self.saveDashboard(
                    dashboardName,
                    dashboardDescription,
                    observeSubCat,
                    observePage
                );
                } catch(error){
                    logger.error(error)
                } finally {
                    return dashboardId;
                }
    }


    addAnalysesToDashboard(cat, subCat, analysesToAdd, observePage) {
        try{
            observePage.clickOnCategory(cat);
            observePage.clickOnSubCategory(subCat); 
    
            analysesToAdd.forEach( analysis => {
                observePage.clickonAddAnalysisIdButton(analysis.analysisId);
                expect(
                  observePage.getRemoveAnalysisByIdElement(analysis.analysisId).isDisplayed
                ).toBeTruthy();
              });
    
              observePage.clickonSaveButton();

        }catch(error){
            logger.error(error)
        }
    }

    saveDashboard(name, description, subCat, observePage) {
        try {
          // Enter name
          observePage.getDashboardNameElement().clear();
          observePage.getDashboardNameElement().sendKeys(name);
         
          // Enter description
          observePage.getDashboardDescElement().clear();
          observePage.getDashboardDescElement().sendKeys(description);

          // Click on category
          observePage.clickOnCategorySelect();

          browser.sleep(2000);
          // Click on subcategory
          observePage.clickOnSubCategorySelect(subCat);
    
          // save the dashboard
          observePage.clickOnSaveDialogButton();

          expect(observePage.getSaveButton().isDisplayed).toBeTruthy();

        } catch (e) {
          logger.error(e);
        } finally {
          expect(
            observePage.getDashboardTitle(name).isDisplayed
          ).toBeTruthy();

          //get dashboard id from current url
          browser.getCurrentUrl().then(url => {
            let dashboardId = url.split('=')[1];
            return dashboardId;
          });
        }
    }

    verifyDashboard(dashboardName, analysisName, del = true) {
        try {
          let _self = this;
          let observePage = new observePage();
          // Verify dashboard name
          expect(
            observePage.getDashboardTitle(dashboardName).isDisplayed
          ).toBeTruthy();

          // Verify added analysis
          expect(
            observePage.dashboard.getAddedAnalysisName(analysisName).isDisplayed
          ).toBeTruthy();

          // Verify dashboard actions
          expect(
            observePage.getDashboardAction('Refresh').isDisplayed
          ).toBeTruthy();
          expect(
            observePage.getDashboardAction('Delete').isDisplayed
          ).toBeTruthy();
          expect(
            observePage.getDashboardAction('Edit').isDisplayed
          ).toBeTruthy();
          expect(
            observePage.getDashboardAction('Filter').isDisplayed
          ).toBeTruthy();

          expect(browser.getCurrentUrl()).toContain('?dashboard');
          if (del) {
            _self.deleteDashboard(dashboardName);
          }
        } catch (e) {
          logger.error(e);
        } finally {
        }
    }

    deleteDashboard(dashboardName) {
        try {
            let observePage = new ObservePage();
          // Delete created dashboard
          observePage.clickOnDeleteDashboardButton();

          // Delete popup
          observePage.clickOnDashboardConfirmDeleteButton();

          commonFunctions.waitFor.elementToBeNotVisible(
            observePage.getDashboardTitle(dashboardName)
          );
          expect(
            observePage.getDashboardTitle(dashboardName).isPresent()
          ).toBeFalsy();

        } catch (e) {
          logger.error(e);
        }
    }

    addAnalysisByApi(
        host,
        token,
        name,
        description,
        analysisType,
        subType,
        filters = null
      ) {
        try {
          let createdAnalysis = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            name,
            description,
            analysisType,
            subType,
            filters
          );
          if (!createdAnalysis) {
            return null;
          }
          let analysisId = createdAnalysis.contents.analyze[0].executionId.split(
            '::'
          )[0];
    
          let analysis = {
            analysisName: name,
            analysisId: analysisId
          };
          return analysis;
        } catch (e) {
          logger.error(e);
        }
    }


}


module.exports = DashboardFunctions;