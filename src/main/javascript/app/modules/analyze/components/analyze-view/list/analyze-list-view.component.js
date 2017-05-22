import template from './analyze-list-view.component.html';

export const AnalyzeListViewComponent = {
  template,
  bindings: {
    reports: '<',
    reportType: '<',
    filter: '<',
    onAction: '&',
    searchTerm: '<',
    updater: '<'
  },
  controller: class AnalyzeListViewController {
    constructor(dxDataGridService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;

      this._gridListInstance = null;
      this.onUpdate = this.onUpdate.bind(this);
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(this.onUpdate);
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate() {
      console.log('new filter');
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
      if (this.reportType === 'all') {
        this._gridListInstance.clearFilter();
      } else {
        this._gridListInstance.filter(['type', '=', this.states.reportType]);
      }
    }

    fork(analysis) {
      this.onAction({
        type: 'fork',
        model: analysis
      });
    }

    execute(analysisId) {
      this.onAction({
        type: 'execute',
        model: analysisId
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
      const dataSource = this.reports || [];
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
