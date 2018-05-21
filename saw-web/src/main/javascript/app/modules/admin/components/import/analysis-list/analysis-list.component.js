import * as template from './analysis-list.component.html';
import * as isUndefined from 'lodash/isUndefined';

export const AnalysisListViewComponent = {
  template,
  bindings: {
    analysisList: '<',
    updater: '<'
  },
  controller: class AnalysisListViewController {
    constructor(dxDataGridService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._gridListInstance = null;
      this.selectAllAnalysis = false;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));
    }

    $onChanges(changedObj) {
      if (!isUndefined(changedObj.analysisList.currentValue)) {
        this.reloadDataGrid(changedObj.analysisList.currentValue);
      }
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({analysisList}) {
      /* eslint-disable */
      analysisList && this.reloadDataGrid(analysisList);
      /* eslint-enable */
    }

    reloadDataGrid(analysisList) {
      if (this._gridListInstance !== null) {
        this._gridListInstance.option('dataSource', analysisList);
        this._gridListInstance.refresh();
      }
    }
    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }
    getGridConfig() {
      const dataSource = this.analysisList || [];
      const columns = [{
        caption: 'Analysis Name',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '40%',
        cellTemplate: 'analysisNameCellTemplate'
      }, {
        caption: 'Analysis Type',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        cellTemplate: 'analysisTypeCellTemplate'
      }, {
        caption: 'Metric Name',
        dataField: 'metricName',
        allowSorting: true,
        alignment: 'left',
        width: '40%',
        cellTemplate: 'metricNameCellTemplate'
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
