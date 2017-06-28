import clone from 'lodash/clone';

import template from './analyze-list-view.component.html';

export const AnalyzeListViewComponent = {
  template,
  bindings: {
    analyses: '<',
    analysisType: '<',
    filter: '<',
    onAction: '&',
    searchTerm: '<',
    updater: '<'
  },
  controller: class AnalyzeListViewController {
    constructor($mdDialog, dxDataGridService, AnalyzeService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._dxDataGridService = dxDataGridService;
      this._AnalyzeService = AnalyzeService;

      this._gridListInstance = null;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));
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
      } else {
        this._gridListInstance.filter(['type', '=', analysisType]);
      }
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
      this.onUpdateAnalysisType(this.analysisType);
    }

    remove(analysis) {
      this.onAction({
        type: 'delete',
        model: analysis
      });
    }

    fork(analysis) {
      this.onAction({
        type: 'fork',
        model: analysis
      });
    }

    openPublishModal(model) {
      const tpl = '<analyze-publish-dialog model="model" on-publish="onPublish(model)"></analyze-publish-dialog>';

      this._$mdDialog
        .show({
          template: tpl,
          controllerAs: '$ctrl',
          controller: scope => {
            scope.model = clone(model);
            scope.onPublish = this.publish.bind(this);
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          clickOutsideToClose: true
        });
    }

    publish(model) {
      this.onAction({
        type: 'publish',
        model
      });
    }

    execute(analysis) {
      this.onAction({
        type: 'execute',
        model: analysis
      });
    }

    edit(analysis) {
      this.onAction({
        type: 'edit',
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
        width: '50%',
        cellTemplate: 'nameCellTemplate'
      }, {
        caption: 'METRICS',
        dataField: 'metrics',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        calculateCellValue: rowData => {
          return rowData.metricName ||
            (rowData.metrics || []).join(', ');
        },
        cellTemplate: 'metricsCellTemplate'
      }, {
        caption: 'SCHEDULED',
        dataField: 'scheduled',
        allowSorting: true,
        alignment: 'left',
        width: '15%'
      }, {
        caption: 'TYPE',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '10%',
        calculateCellValue: rowData => {
          return (rowData.type || '').toUpperCase();
        },
        cellTemplate: 'typeCellTemplate'
      }, {
        caption: '',
        cellTemplate: 'actionCellTemplate'
      }];

      return this._dxDataGridService.mergeWithDefaultConfig({
        onInitialized: this.onGridInitialized.bind(this),
        columns,
        dataSource,
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
