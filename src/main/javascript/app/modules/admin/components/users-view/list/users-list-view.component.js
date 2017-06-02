import template from './users-list-view.component.html';
import isUndefined from 'lodash/isUndefined';

export const UsersListViewComponent = {
  template,
  bindings: {
    users: '<',
    customer: '<',
    searchTerm: '<'
  },
  controller: class UsersListViewController {
    constructor(dxDataGridService) {
      'ngInject';
      this._dxDataGridService = dxDataGridService;
      this._gridListInstance = null;
    }

    $onInit() {
      this.gridConfig = this.getGridConfig();
    }

    $onChanges(changedObj) {
      if (!isUndefined(changedObj.users.currentValue)) {
        this.reloadDataGrid(changedObj.users.currentValue);
      }
    }

    reloadDataGrid(users) {
      this._gridListInstance.option('dataSource', users);
      this._gridListInstance.refresh();
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }

    getGridConfig() {
      const dataSource = this.users || [];
      const columns = [{
        caption: 'USER ID',
        dataField: 'masterLoginId',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        cellTemplate: 'userIdCellTemplate'
      }, {
        caption: 'ROLE',
        dataField: 'roleName',
        allowSorting: true,
        alignment: 'left',
        width: '10%',
        cellTemplate: 'roleTypeCellTemplate'
      }, {
        caption: 'FIRST NAME',
        dataField: 'firstName',
        allowSorting: true,
        alignment: 'left',
        width: '18%',
        cellTemplate: 'firstNameCellTemplate'
      }, {
        caption: 'LAST NAME',
        dataField: 'lastName',
        allowSorting: true,
        alignment: 'left',
        width: '18%',
        cellTemplate: 'lastNameCellTemplate'
      }, {
        caption: 'EMAIL',
        dataField: 'email',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        cellTemplate: 'emailCellTemplate'
      }, {
        caption: 'STATUS',
        dataField: 'activeStatusInd',
        allowSorting: true,
        alignment: 'left',
        width: '8%',
        cellTemplate: 'statusCellTemplate'
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
