import * as template from './export-list.component.html';
import * as isUndefined from 'lodash/isUndefined';

export const ExportListViewComponent = {
  template,
  bindings: {
    analysisList: '<',
    updater: '<',
    onAction: '&'
  },
  controller: class ExportListViewController {
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

    editRow(row, flag) {
      this.analysisList.forEach(analysis => {
        if (row.data.analysis.name === analysis.analysis.name && row.data.analysis.id === analysis.analysis.id) {
          analysis.selection = !flag;
        }
      });
    }
    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }
    selectAll(flag) {
      this.analysisList.forEach(analysis => {
        analysis.selection = !flag;
      });
    }
    doExport(analysisList) {
      this.selectAllAnalysis = false;
      this.onAction({
        type: 'export',
        model: analysisList
      });
    }
    validation() {
      if (this.analysisList.length > 0) {
        return false;
      }
      return true;
    }
    getGridConfig() {
      const dataSource = this.analysisList || [];
      const columns = [{
        caption: 'Select All to Export',
        dataField: 'selection',
        allowSorting: false,
        alignment: 'center',
        width: '10%',
        headerCellTemplate: '<md-checkbox ng-click="$ctrl.selectAll($ctrl.selectAllAnalysis)" ng-model="$ctrl.selectAllAnalysis">Select All to Export</md-checkbox>',
        cellTemplate: 'selectionCellTemplate'
      }, {
        caption: 'Analysis Name',
        dataField: 'analysis.name',
        allowSorting: true,
        alignment: 'left',
        width: '40%',
        cellTemplate: 'analysisNameCellTemplate'
      }, {
        caption: 'Analysis Type',
        dataField: 'analysis.type',
        allowSorting: true,
        alignment: 'left',
        width: '16%',
        cellTemplate: 'analysisTypeCellTemplate'
      }, {
        caption: 'Metric Name',
        dataField: 'analysis.metricName',
        allowSorting: true,
        alignment: 'left',
        width: '34%',
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
