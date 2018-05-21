import * as template from './import-file-list.component.html';
import * as isUndefined from 'lodash/isUndefined';

export const ImportFileListViewComponent = {
  template,
  bindings: {
    fileList: '<',
    updater: '<',
    onAction: '&'
  },
  controller: class ImportFileListViewController {
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
      if (!isUndefined(changedObj.fileList.currentValue)) {
        this.reloadDataGrid(changedObj.fileList.currentValue);
      }
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({fileList}) {
      /* eslint-disable */
      fileList && this.reloadDataGrid(fileList);
      /* eslint-enable */
    }

    reloadDataGrid(fileList) {
      if (this._gridListInstance !== null) {
        this._gridListInstance.option('dataSource', fileList);
        this._gridListInstance.refresh();
      }
    }
    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }
    getGridConfig() {
      const dataSource = this.fileList || [];
      const columns = [{
        caption: 'File Name',
        dataField: 'name',
        allowSorting: false,
        alignment: 'center',
        width: '50%',
        cellTemplate: 'fileNameCellTemplate'
      }, {
        caption: 'Analysis Count',
        dataField: 'count',
        allowSorting: true,
        alignment: 'left',
        width: '50%',
        cellTemplate: 'analysisCountCellTemplate'
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
