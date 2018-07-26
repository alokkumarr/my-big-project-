import * as template from './import-list.component.html';
import * as isUndefined from 'lodash/isUndefined';

export const ImportListViewComponent = {
  template,
  bindings: {
    analysisList: '<',
    importFlag: '<',
    updater: '<',
    importFlagUpdater: '<',
    onAction: '&'
  },
  controller: class ImportListViewController {
    constructor(dxDataGridService, $mdDialog) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._$mdDialog = $mdDialog;
      this._gridListInstance = null;
      this.selectAllAnalysis = false;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));
      this.importFlagUpdaterSubscribtion = this.importFlagUpdater.subscribe(update => this.onimportFlagUpdater(update));
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

    onimportFlagUpdater({flag}) {
      this.importFlag = flag;
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
          analysis.overrideInd = false;
        }
      });
    }
    overrideAnalysis(row) {
      this.analysisList.forEach(analysis => {
        if (row.data.analysis.name === analysis.analysis.name && row.data.analysis.id === analysis.analysis.id) {
          analysis.selection = true;
          if (isUndefined(analysis.overrideInd)) {
            analysis.overrideInd = true;
          } else {
            analysis.overrideInd = !analysis.overrideInd;
          }
        }
      });
    }
    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }
    selectAll(flag) {
      this.analysisList.forEach(analysis => {
        analysis.selection = !flag;
        analysis.overrideInd = false;
      });
    }
    doImport(analysisList) {
      this.selectAllAnalysis = false;
      this.onAction({
        type: 'import',
        model: analysisList
      });
    }
    doLogExport(analysisList) {
      this.onAction({
        type: 'exportLog',
        model: analysisList
      });
    }
    displayError(error) {
      const alert = this._$mdDialog.alert()
        .title('ERROR :')
        .textContent(angular.toJson(error.data || error))
        .ok('Ok');
      this._$mdDialog.show(alert);
    }

    validation() {
      if (this.analysisList.length > 0) {
        return false;
      }
      return true;
    }
    importValidation() {
      if (this.analysisList.length > 0 && this.importFlag === 0) {
        return false;
      }
      return true;
    }
    getGridConfig() {
      const dataSource = this.analysisList || [];
      const columns = [{
        caption: 'Select All to Import',
        dataField: 'selection',
        allowSorting: false,
        alignment: 'left',
        width: '10%',
        headerCellTemplate: '<md-checkbox ng-click="$ctrl.selectAll($ctrl.selectAllAnalysis)" ng-model="$ctrl.selectAllAnalysis">Select All to Import</md-checkbox>',
        cellTemplate: 'selectionCellTemplate'
      }, {
        caption: 'Analysis Name',
        dataField: 'analysis.name',
        allowSorting: true,
        alignment: 'left',
        width: '30%',
        cellTemplate: 'analysisNameCellTemplate'
      }, {
        caption: 'Analysis Type',
        dataField: 'analysis.type',
        allowSorting: true,
        alignment: 'left',
        width: '10%',
        cellTemplate: 'analysisTypeCellTemplate'
      }, {
        caption: 'Metric Name',
        dataField: 'analysis.metricName',
        allowSorting: true,
        alignment: 'left',
        width: '30%',
        cellTemplate: 'metricNameCellTemplate'
      }, {
        caption: 'Logs',
        dataField: 'log',
        allowSorting: false,
        alignment: 'left',
        width: '20%',
        cellTemplate: 'logCellTemplate'
      }];
      return this._dxDataGridService.mergeWithDefaultConfig({
        onInitialized: this.onGridInitialized.bind(this),
        columns,
        dataSource,
        wordWrapEnabled: true,
        scrolling: {
          scrollingEnabled: false
        },
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
