import * as template from './categories-list-view.component.html';
import * as isUndefined from 'lodash/isUndefined';

export const CategoriesListViewComponent = {
  template,
  bindings: {
    categories: '<',
    updater: '<',
    customer: '<',
    onAction: '&',
    searchTerm: '<'
  },
  controller: class CategoriesListViewController {
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
      if (!isUndefined(changedObj.categories.currentValue)) {
        this.reloadDataGrid(changedObj.categories.currentValue);
      }
    }

    $onDestroy() {
      this.updaterSubscribtion.unsubscribe();
    }

    onUpdate({categories}) {
      /* eslint-disable */
      categories && this.reloadDataGrid(categories);
      /* eslint-enable */
    }

    reloadDataGrid(categories) {
      this._gridListInstance.option('dataSource', categories);
      this._gridListInstance.refresh();
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }

    openDeleteModal(category) {
      this.onAction({
        type: 'delete',
        model: category
      });
    }

    openEditModal(category) {
      this.onAction({
        type: 'edit',
        model: category
      });
    }

    getGridConfig() {
      const dataSource = this.categories || [];
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
        width: '20%',
        cellTemplate: 'categoryCellTemplate'
      }, {
        caption: 'SUB CATEGORIES',
        dataField: 'subCategories',
        allowSorting: true,
        alignment: 'left',
        width: '52%',
        cellTemplate: 'subCategoriesCellTemplate'
      }, {
        caption: '',
        width: '8%',
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
