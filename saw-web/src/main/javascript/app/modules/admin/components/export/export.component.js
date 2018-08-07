import * as template from './export.component.html';
import style from './export.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';
import * as JSZip from 'jszip';
import {AdminMenuData} from '../../consts';
import * as isUndefined from 'lodash/isUndefined';
import * as get from 'lodash/get';
import {Subject} from 'rxjs/Subject';

let self;
export const AnalysisExportComponent = {
  template,
  styles: [style],
  controller: class AnalysisExportComponent extends AbstractComponentController {
    constructor($timeout, $componentHandler, $injector, $compile, $state, $mdDialog, $filter,
      $mdToast, JwtService, $window, $rootScope, ExportService, CategoriesManagementService) {
      'ngInject';
      super($injector);
      this.$componentHandler = $componentHandler;
      this._exportService = ExportService;
      this._$filter = $filter;
      this._$rootScope = $rootScope;
      this._JwtService = JwtService;
      this._CategoriesManagementService = CategoriesManagementService;
      this.updater = new Subject();
      this._$timeout = $timeout;
      self = this;
    }

    $onInit() {
      this._$timeout(() => {
        const leftSideNav = self.$componentHandler.get('left-side-nav')[0];
        leftSideNav.update(AdminMenuData, 'ADMIN');
      });
      this.metrics = [];
      this.analysisTableList = [];
      this.categoriesMap = [];
      this.getMetricList();
      this.getAllCategories();
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

    getAllCategories() {
      this._$rootScope.showProgress = true;
      const id = get(this._JwtService.getTokenObj(), 'ticket.custID');
      this._CategoriesManagementService.getActiveCategoriesList(id).then(response => {
        this.categoriesMap = response.categories.reduce((map, tag) => {
          if (tag.moduleName === 'ANALYZE') {
            tag.subCategories.forEach(subCategory => {
              map[subCategory.subCategoryId] = subCategory.subCategoryName;
            });
          }
          return map;
        }, {});
        this._$rootScope.showProgress = false;
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }

    getMetricIds(metricNames) {
      const ids = [];
      for (let i = 0; this.metrics.length > i; i++) {
        for (let j = 0; metricNames.length > j; j++) {
          if (this.metrics[i].metricName === metricNames[j]) {
            ids.push(this.metrics[i].id);
          }
        }
      }
      return ids;
    }

    getAnalysisByMetricIds(metricNames) {
      this.analysisTableList = [];
      if (!isUndefined(metricNames) && metricNames.length > 0) {
        this._$rootScope.showProgress = true;
        this.list = this.getMetricIds(metricNames);
        const id = get(this._JwtService.getTokenObj(), 'ticket.custCode');
        const contentsObject = {
          keys: [],
          action: 'export'
        };
        this.list.forEach(selectedSemanticid => {
          const keyObject = {
            customerCode: id,
            module: 'ANALYZE',
            semanticId: selectedSemanticid
          };
          contentsObject.keys.push(keyObject);
        });
        const body = {
          contents: contentsObject
        };
        this._exportService.getAnalysisByMetricIds(body).then(analysis => {
          if (analysis.data.contents.analyze.length > 0) {
            analysis.data.contents.analyze.forEach(element => {
              if (!isUndefined(element.categoryId) && !isUndefined(element.name) && element.name !== '') {
                this.analysisTableObject = {
                  selection: false,
                  analysis: {},
                  categoryName: ''
                };
                this.analysisTableObject.categoryName = this.categoriesMap[element.categoryId];
                this.analysisTableObject.analysis = element;
                this.analysisTableList.push(this.analysisTableObject);
              }
              this.updater.next({analysisList: this.analysisTableList});
            });
          } else {
            this.analysisTableList = [];
          }
          this._$rootScope.showProgress = false;
        }).catch(() => {
          this._$rootScope.showProgress = false;
        });
      }
    }
    onListAction(actionType, payload) {
      switch (actionType) {
      case 'export':
        this.export(payload);
        break;
      default:
      }
    }
    export(analysisList) {
      const exportList = [];
      analysisList.forEach(analysis => {
        if (analysis.selection) {
          exportList.push(analysis.analysis);
        }
      });
      if (exportList.length > 0) {
        const zip = new JSZip();
        const FileSaver = require('file-saver');
        const fileExportList = this.splitAnalysisOnMetric(exportList, this.seletedMetrics);
        fileExportList.forEach(exportAnalysis => {
          if (exportAnalysis.analysisList.length > 0) {
            zip.file(`${exportAnalysis.fileName}.json`,
              new Blob([angular.toJson(exportAnalysis.analysisList)], {type: 'application/json;charset=utf-8'}));
          }
        });
        zip.generateAsync({type: 'blob'}).then(content => {
          let zipFileName = this.getFileName('');
          zipFileName = zipFileName.replace('_', '');
          FileSaver.saveAs(content, `${zipFileName}.zip`);
        });
      }
    }
    splitAnalysisOnMetric(exportAnalysisList, metricNames) {
      const exportList = [];
      metricNames.forEach(name => {
        const exportAnalysis = {
          fileName: '',
          analysisList: []
        };
        exportAnalysis.fileName = this.getFileName(name);
        exportAnalysisList.forEach(analysis => {
          if (analysis.metricName === name) {
            exportAnalysis.analysisList.push(analysis);
          }
        });
        exportList.push(exportAnalysis);
      });
      return exportList;
    }
    getFileName(name) {
      const d = new Date();
      const formatedDate = this._$filter('date')(d, 'yyyyMMddHHmmss');
      const custCode = get(this._JwtService.getTokenObj(), 'ticket.custCode');
      name = name.replace(' ', '_');
      name = name.replace('\\', '-');
      name = name.replace('/', '-');
      return custCode + '_' + name + '_' + formatedDate;
    }
  }
};
