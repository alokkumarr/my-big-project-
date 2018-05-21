import * as template from './export.component.html';
import style from './export.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';
import * as JSZip from 'jszip';
import {AdminMenuData} from '../../consts';
import * as isUndefined from 'lodash/isUndefined';
import * as get from 'lodash/get';
import {Subject} from 'rxjs/Subject';

export const AnalysisExportComponent = {
  template,
  styles: [style],
  controller: class AnalysisExportComponent extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $filter,
      $mdToast, JwtService, $window, $rootScope, ExportService) {
      'ngInject';
      super($injector);
      this.$componentHandler = $componentHandler;
      this._exportService = ExportService;
      this._$filter = $filter;
      this._$rootScope = $rootScope;
      this._JwtService = JwtService;
      this.updater = new Subject();
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];
      leftSideNav.update(AdminMenuData, 'ADMIN');
      this.metrics = [];
      this.analysisTableList = [];
      this.getMetricList();
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
          keys: [{
            customerCode: id,
            module: 'ANALYZE'
          }],
          action: 'export'
        };
        const body = {
          contents: contentsObject
        };
        this._exportService.getAnalysisByMetricIds(body).then(analysis => {
          this.analysisTableList = [];
          if (analysis.data.contents.analyze.length > 0) {
            analysis.data.contents.analyze.forEach(element => {
              if (element.categoryId !== null && element.name !== null) {
                this.list.forEach(id => {
                  if (id === element.semanticId) {
                    this.analysisTableObject = {
                      selection: false,
                      analysis: {}
                    };
                    this.analysisTableObject.analysis = element;
                    this.analysisTableList.push(this.analysisTableObject);
                  }
                });
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
          const custCode = get(this._JwtService.getTokenObj(), 'ticket.custCode');
          FileSaver.saveAs(content, `${custCode}.zip`);
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
