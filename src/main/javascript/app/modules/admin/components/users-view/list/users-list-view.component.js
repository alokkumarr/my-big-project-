import template from './users-list-view.component.html';
import isUndefined from 'lodash/isUndefined';

export const UsersListViewComponent = {
  template,
  bindings: {
    users: '<',
    updater: '<',
    customer: '<',
    onAction: '&',
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
      this.updaterSubscribtion = this.updater.subscribe(update => this.onUpdate(update));
    }

    $onChanges(changedObj) {
      if (!isUndefined(changedObj.users.currentValue)) {
        this.reloadDataGrid(changedObj.users.currentValue);
      }
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({users}) {
      /* eslint-disable */
      users && this.reloadDataGrid(users);
      /* eslint-enable */
    }

    reloadDataGrid(users) {
      this._gridListInstance.option('dataSource', users);
      this._gridListInstance.refresh();
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }

    openDeleteModal(user) {
      this.onAction({
        type: 'delete',
        model: user
      });
    }

    openEditModal(user) {
      this.onAction({
        type: 'edit',
        model: user
      });
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
