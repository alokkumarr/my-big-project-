import * as isEmpty from 'lodash/isEmpty';

import * as template from './analyze-list-view.component.html';
import style from './analyze-list-view.component.scss';

export const AnalyzeListViewComponent = {
  template,
  styles: [style],
  bindings: {
    analyses: '<',
    analysisType: '<',
    filter: '<',
    onAction: '&',
    searchTerm: '<',
    updater: '<'
  },
  controller: class AnalyzeListViewController {
    constructor($mdDialog, dxDataGridService, AnalyzeService, AnalyzeActionsService, JwtService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._dxDataGridService = dxDataGridService;
      this._AnalyzeService = AnalyzeService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._JwtService = JwtService;

      this._gridListInstance = null;

      this.canUserFork = false;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));

      this.canUserFork = this._JwtService.hasPrivilege('FORK', {
        subCategoryId: this.analyses[0].categoryId
      });
    }

    showExecutingFlag(analysisId) {
      return analysisId && this._AnalyzeService.isExecuting(analysisId);
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({analysisType, analyses}) {
      /* eslint-disable */
      analysisType && this.onUpdateAnalysisType(analysisType);
      analyses && this.reloadDataGrid(analyses);
      /* eslint-enable */
    }

    onUpdateAnalysisType(analysisType) {
      if (analysisType === 'all') {
        this._gridListInstance.clearFilter();
      } else if (analysisType === 'scheduled') {
        this._gridListInstance.filter(itemData => {
          return !isEmpty(itemData.scheduleHuman);
        });
      } else {
        this._gridListInstance.filter(['type', '=', analysisType]);
      }
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
      this.onUpdateAnalysisType(this.analysisType);
    }

    fork(analysis) {
      this._AnalyzeActionsService.fork(analysis);
    }

    onSuccessfulDeletion(analysis) {
      this.onAction({
        type: 'onSuccessfulDeletion',
        model: analysis
      });
    }

    onSuccessfulExecution(analysis) {
      this.onAction({
        type: 'onSuccessfulExecution',
        model: analysis
      });
    }

    onSuccessfulPublish(analysis) {
      this.onAction({
        type: 'onSuccessfulPublish',
        model: analysis
      });
    }

    reloadDataGrid(analyses) {
      this._gridListInstance.option('dataSource', analyses);
      this._gridListInstance.refresh();
    }

    getGridConfig() {
      const dataSource = this.analyses || [];
      const columns = [{
        caption: 'NAME',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '36%',
        cellTemplate: 'nameCellTemplate',
        cssClass: 'branded-column-name'
      }, {
        caption: 'METRICS',
        dataField: 'metrics',
        allowSorting: true,
        alignment: 'left',
        width: '21%',
        calculateCellValue: rowData => {
          return rowData.metricName ||
            (rowData.metrics || []).join(', ');
        },
        cellTemplate: 'metricsCellTemplate'
      }, {
        caption: 'SCHEDULED',
        dataField: 'scheduleHuman',
        allowSorting: true,
        alignment: 'left',
        width: '12%'
      }, {
        caption: 'TYPE',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '8%',
        calculateCellValue: rowData => {
          return (rowData.type || '').toUpperCase();
        },
        cellTemplate: 'typeCellTemplate'
      }, {
        caption: 'CREATOR',
        dataField: 'userFullName',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        calculateCellValue: rowData => {
          return (rowData.userFullName || '').toUpperCase();
        },
        cellTemplate: 'creatorCellTemplate'
      }, {
        caption: 'CREATED',
        allowSorting: true,
        dataField: 'createdTimestamp',
        alignment: 'left',
        width: '8%',
        cellTemplate: 'timecreatedCellTemplate'
      }, {
        caption: '',
        cellTemplate: 'actionCellTemplate'
      }];
      const wordWrapEnabled = true;
      return this._dxDataGridService.mergeWithDefaultConfig({
        onInitialized: this.onGridInitialized.bind(this),
        columns,
        dataSource,
        wordWrapEnabled,
        paging: {
          pageSize: 10
        },
        pager: {
          showPageSizeSelector: true,
          showInfo: true
        }
      });
    }
  }
};
