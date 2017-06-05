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
        caption: 'ID',
        dataField: 'id',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      }, {
        caption: 'DATE',
        dataField: 'finished',
        dataType: 'date',
        sortOrder: 'desc',
        format: 'shortDateShortTime',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      }, {
        caption: 'STATUS',
        dataField: 'status',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
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
      this._$state.go('analyze.publishedDetail', {
        publishId: analysis.PUBLISHED_ANALYSIS_ID,
        analysisId: analysis.ANALYSIS_ID
      });
    }
  }
};
