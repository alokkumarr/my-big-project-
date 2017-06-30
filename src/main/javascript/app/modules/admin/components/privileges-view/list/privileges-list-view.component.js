import template from './privileges-list-view.component.html';
import isUndefined from 'lodash/isUndefined';

export const PrivilegesListViewComponent = {
  template,
  bindings: {
    privileges: '<',
    updater: '<',
    customer: '<',
    onAction: '&',
    searchTerm: '<'
  },
  controller: class PrivilegesListViewController {
    constructor(dxDataGridService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._gridListInstance = null;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));
    }

    $onChanges(changedObj) {
      if (!isUndefined(changedObj.privileges.currentValue)) {
        this.reloadDataGrid(changedObj.privileges.currentValue);
      }
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({privileges}) {
      /* eslint-disable */
      privileges && this.reloadDataGrid(privileges);
      /* eslint-enable */
    }

    reloadDataGrid(privileges) {
      this._gridListInstance.option('dataSource', privileges);
      this._gridListInstance.refresh();
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }

    openDeleteModal(privilege) {
      this.onAction({
        type: 'delete',
        model: privilege
      });
    }

    openEditModal(privilege) {
      this.onAction({
        type: 'edit',
        model: privilege
      });
    }

    getGridConfig() {
      const dataSource = this.privileges || [];
      const columns = [{
        caption: 'PRODUCT',
        dataField: 'productName',
        allowSorting: true,
        alignment: 'left',
        width: '15%',
        cellTemplate: 'productCellTemplate'
      }, {
        caption: 'MODULE',
        dataField: 'moduleName',
        allowSorting: true,
        alignment: 'left',
        width: '15%',
        cellTemplate: 'moduleCellTemplate'
      }, {
        caption: 'CATEGORY',
        dataField: 'categoryName',
        allowSorting: true,
        alignment: 'left',
        width: '15%',
        cellTemplate: 'categoryCellTemplate'
      }, {
        caption: 'ROLE',
        dataField: 'roleName',
        allowSorting: true,
        alignment: 'left',
        width: '14%',
        cellTemplate: 'roleCellTemplate'
      }, {
        caption: 'PRIVILEGE DESC',
        dataField: 'privilegeDesc',
        allowSorting: true,
        alignment: 'left',
        width: '35%',
        cellTemplate: 'privilegeDescCellTemplate'
      }, {
        caption: '',
        width: '6%',
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
