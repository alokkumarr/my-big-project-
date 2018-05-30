import * as template from './import.component.html';
import style from './import.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';
import {AdminMenuData} from '../../consts';
import * as isUndefined from 'lodash/isUndefined';
import * as get from 'lodash/get';
import {Subject} from 'rxjs/Subject';

let self;
export const AnalysisImportComponent = {
  template,
  styles: [style],
  controller: class AnalysisImportComponent extends AbstractComponentController {
    constructor($timeout, $componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, CategoriesManagementService,
      $window, $rootScope, LocalSearchService, ImportService, ExportService, $filter) {
      'ngInject';
      super($injector);
      this.$componentHandler = $componentHandler;
      this._$rootScope = $rootScope;
      this._$filter = $filter;
      this._$mdToast = $mdToast;
      this._JwtService = JwtService;
      this._CategoriesManagementService = CategoriesManagementService;
      this._exportService = ExportService;
      this._ImportService = ImportService;
      this.metrics = [];
      this.categoryAnalysisTableList = [];
      this.fileTableList = [];
      this.analysisUpdater = new Subject();
      this.updater = new Subject();
      this.fileListupdater = new Subject();
      this.categories = this.getAllCategories();
      this.getMetricList();
      this._$timeout = $timeout;
      self = this;
    }
    getAllCategories() {
      this._$rootScope.showProgress = true;
      const id = get(this._JwtService.getTokenObj(), 'ticket.custID');
      this._CategoriesManagementService.getActiveCategoriesList(id).then(response => {
        this.dataHolder = response.categories;
        this._$rootScope.showProgress = false;
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }
    getAllAnalysisByCategoryId(subCategoryId) {
      this._$rootScope.showProgress = true;
      const id = String(subCategoryId);
      this._ImportService.getAnalysesFor(id)
        .then(response => {
          this.categoryAnalysisTableList = response.data.contents.analyze;
          this.checkMetricOfAnalysis();
          this.checkDuplicationAnalysis();
          this.updater.next({analysisList: this.analysisTableList});
          this.analysisUpdater.next({analysisList: this.categoryAnalysisTableList});
          this._$rootScope.showProgress = false;
        }).catch(() => {
          this._$rootScope.showProgress = false;
        });
    }
    $onInit() {
      this._$timeout(() => {
        const leftSideNav = self.$componentHandler.get('left-side-nav')[0];
        leftSideNav.update(AdminMenuData, 'ADMIN');
      });
      this.files = [];
      this.analysisTableList = [];
    }
    getMetricList() {
      this._$rootScope.showProgress = true;
      this._exportService.getMetricList().then(metrics => {
        this.metrics = metrics;
        this._$rootScope.showProgress = false;
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }
    readFiles() {
      this._$rootScope.showProgress = true;
      this.analysisTableList = [];
      this.fileTableList = [];
      this.files.forEach(file => {
        if (file.type === 'application/json') {
          const reader = new FileReader();
          reader.onload = (theFile => {
            return e => {
              let list = [];
              list = angular.fromJson(e.srcElement.result);
              const fileObject = {
                name: '',
                count: 0
              };
              fileObject.name = theFile.name;
              fileObject.count = list.length;
              this.fileTableList.push(fileObject);
              list.forEach(analysis => {
                this.analysisTableObject = {
                  selection: false,
                  analysis: {}
                };
                this.analysisTableObject.analysis = analysis;
                this.analysisTableList.push(this.analysisTableObject);
              });
              this.checkMetricOfAnalysis();
              this.checkDuplicationAnalysis();
              this.updater.next({analysisList: this.analysisTableList});
              this.fileListupdater.next({fileList: this.fileTableList});
            };
          })(file);
          reader.readAsText(file);
        } else {
          this._$mdToast.show({
            template: '<md-toast><span>' + file.name + ' not a JSON file.</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
        this._$rootScope.showProgress = false;
      });
      this._$rootScope.showProgress = false;
    }
    checkDuplicationAnalysis() {
      if (this.analysisTableList.length > 0 && this.categoryAnalysisTableList.length > 0) {
        this.categoryAnalysisTableList.forEach(analysis => {
          this.analysisTableList.forEach(analysisObject => {
            if (analysisObject.analysis.name === analysis.name && !analysisObject.noMetricInd) {
              analysisObject.duplicateAnalysisInd = true;
              analysisObject.logColor = 'red';
              analysisObject.errorMsg = 'Analysis already exists.';
              analysisObject.errorInd = false;
              analysisObject.importInd = false;
              this.changeExistsAnalysisParameter(analysisObject.analysis, analysis);
              analysisObject.log = 'Analysis already exists.';
            }
          });
        });
      }
      if (this.analysisTableList.length > 0 && this.categoryAnalysisTableList.length === 0) {
        this.checkMetricOfAnalysis();
      }
    }
    changeNewAnalysisParameter(importFileAnalysis, maprDbAnalysis) {
      importFileAnalysis.isScheduled = maprDbAnalysis.isScheduled;
      importFileAnalysis.scheduled = maprDbAnalysis.scheduled;
      importFileAnalysis.createdTimestamp = maprDbAnalysis.createdTimestamp;
      importFileAnalysis.id = maprDbAnalysis.id;
      importFileAnalysis.userFullName = maprDbAnalysis.userFullName;
      importFileAnalysis.userId = maprDbAnalysis.userId;
      importFileAnalysis.esRepository = maprDbAnalysis.esRepository;
      importFileAnalysis.repository = maprDbAnalysis.repository;
    }
    changeExistsAnalysisParameter(importFileAnalysis, maprDbAnalysis) {
      importFileAnalysis.isScheduled = maprDbAnalysis.isScheduled;
      importFileAnalysis.scheduled = maprDbAnalysis.scheduled;
      importFileAnalysis.createdTimestamp = maprDbAnalysis.createdTimestamp;
      importFileAnalysis.id = maprDbAnalysis.id;
      importFileAnalysis.userFullName = get(this._JwtService.getTokenObj(), 'ticket.userFullName');
      importFileAnalysis.userId = get(this._JwtService.getTokenObj(), 'ticket.userId');
      importFileAnalysis.esRepository = maprDbAnalysis.esRepository;
      importFileAnalysis.repository = maprDbAnalysis.repository;
    }
    checkMetricOfAnalysis() {
      if (this.analysisTableList.length > 0) {
        this.analysisTableList.forEach(analysisObject => {
          this.flag = 0;
          this.metrics.forEach(checkMetric => {
            if (checkMetric.metricName === analysisObject.analysis.metricName) {
              analysisObject.analysis.semanticId = checkMetric.id;
              this.flag = 1;
            }
          });
          if (this.flag === 0) {
            analysisObject.noMetricInd = true;
            analysisObject.logColor = 'red';
            analysisObject.errorMsg = 'Metric not exists.';
            analysisObject.errorInd = true;
            analysisObject.log = 'Metric not exists.';
          } else {
            analysisObject.duplicateAnalysisInd = false;
            analysisObject.noMetricInd = false;
            analysisObject.importInd = false;
            analysisObject.logColor = 'green';
            analysisObject.log = '';
            analysisObject.errorMsg = '';
            analysisObject.errorInd = false;
          }
        });
      }
    }
    onListAction(actionType, payload) {
      switch (actionType) {
      case 'import':
        this.doImport(payload);
        break;
      case 'exportLog':
        this.exportAllLogs(payload);
      default:
      }
    }
    doImport(analysisList) {
      if (isUndefined(this.categoryId)) {
        this._$mdToast.show({
          template: '<md-toast><span>Target-Category not selected.</md-toast>',
          position: 'top left',
          toastClass: 'toast-primary'
        });
        return;
      }
      let importCount = 0;
      analysisList.forEach(analysisObject => {
        if (analysisObject.selection && !analysisObject.noMetricInd && !analysisObject.importInd) {
          if (analysisObject.overrideInd && analysisObject.duplicateAnalysisInd) {
            this.updateOldAnalysis(analysisObject.analysis);
          }
          if (!analysisObject.duplicateAnalysisInd) {
            this.createNewAnalysis(analysisObject.analysis);
          }
          importCount++;
        }
      });
      if (importCount === 0) {
        this._$mdToast.show({
          template: '<md-toast><span>select analysis to import.</md-toast>',
          position: 'top left',
          toastClass: 'toast-primary'
        });
      }
    }
    createNewAnalysis(analysis) {
      this._$rootScope.showProgress = true;
      this._ImportService.createAnalysis(analysis.semanticId, analysis.type).then(initAnalysis => {
        this.changeNewAnalysisParameter(analysis, initAnalysis);
        this.updateOldAnalysis(analysis);
        this._$rootScope.showProgress = false;
      }).catch(error => {
        this.updateLogs(analysis.name, 'Error While Importing', error, 'red', false, true);
        this._$rootScope.showProgress = false;
      });
    }
    updateOldAnalysis(analysis) {
      this._$rootScope.showProgress = true;
      analysis.categoryId = this.categoryId;
      this._ImportService.updateAnalysis(analysis).then(savedAnalysis => {
        this.updateLogs(savedAnalysis.name, 'Successfully Imported', '', 'green', true, false);
        this._$rootScope.showProgress = false;
      }).catch(error => {
        this.updateLogs(analysis.name, 'Error While Importing', error, 'red', false, true);
        this._$rootScope.showProgress = false;
      });
    }
    updateLogs(analysisName, logShortMsg, logLongMsg, logColor, importFlag, errorFlag) {
      this.analysisTableList.forEach(analysisObject => {
        if (analysisObject.analysis.name === analysisName) {
          analysisObject.log = logShortMsg;
          analysisObject.errorMsg = logLongMsg;
          analysisObject.errorInd = errorFlag;
          analysisObject.logColor = logColor;
          analysisObject.importInd = importFlag;
        }
      });
      this.updater.next({analysisList: this.analysisTableList});
    }
    exportAllLogs(analysisList) {
      const exportList = [];
      analysisList.forEach(analysis => {
        if (analysis.errorInd) {
          const logObject = {
            analysisName: '',
            analysisType: '',
            metricName: '',
            errorType: '',
            errorMessage: ''
          };
          logObject.analysisName = analysis.analysis.name;
          logObject.analysisType = analysis.analysis.type || '';
          logObject.metricName = analysis.analysis.metricName;
          logObject.errorType = analysis.errorMsg.data.error.type;
          logObject.errorMessage = analysis.errorMsg.data.error.message;
          exportList.push(logObject);
        }
      });
      if (exportList.length > 0) {
        const converter = require('json-2-csv');
        converter.json2csv(exportList, (err, csv) => {
          if (err) throw err;
          const FileSaver = require('file-saver');
          const logFileName = this.getLogFileName();
          const newData = new Blob([csv], {type: 'text/csv;charset=utf-8'});
          FileSaver.saveAs(newData, logFileName);
        });
      }
    }
    getLogFileName() {
      const d = new Date();
      const formatedDate = this._$filter('date')(d, 'yyyyMMddHHmmss');
      return 'log' + formatedDate + '.csv';
    }
  }
};
