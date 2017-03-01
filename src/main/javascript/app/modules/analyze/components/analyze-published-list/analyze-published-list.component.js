import ArrayStore from 'devextreme/data/array_store';

import template from './analyze-published-list.component.html';
// import style from './analyze-published-list.component.scss';

export const AnalyzePublishedListComponent = {
  template,
  // styles: [style],
  bindings: {
    analyses: '<'
  },
  controller: class AnalyzePublishedListController {
    constructor(AnalyzeService, $state, $window, dxDataGridService) {
      'ngInject';
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$window = $window;
      const columns = [{
        caption: 'NAME',
        dataField: 'ANALYSIS_NAME',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      }, {
        caption: 'CREATED BY',
        dataField: 'CREATED_USER',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: 'DATE',
        dataField: 'CREATED_DATE',
        dataType: 'date',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: 'IS SCHEDULED',
        dataField: 'IS_SCHEDULED',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: '',
        width: '30%',
        cellTemplate: 'actionCellTemplate'
      }];
      this.gridConfig = dxDataGridService.mergeWithDefaultConfig({
        onRowClick: row => {
          this.goToAnalysis(row.data);
        },
        columns,
        bindingOptions: {
          dataSource: '$ctrl.analyses'
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

    goToAnalysis(analysis) {
      this._$state.go('analyze.publishedDetail',
        {
          publishId: analysis.PUBLISHED_ANALYSIS_ID
        }
      );
    }
  }
};
