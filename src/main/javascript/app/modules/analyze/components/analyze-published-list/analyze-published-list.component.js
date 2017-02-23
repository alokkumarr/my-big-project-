import ArrayStore from 'devextreme/data/array_store';

import template from './analyze-published-list.component.html';
// import style from './analyze-published-list.component.scss';

export const AnalyzePublishedListComponent = {
  template,
  // styles: [style],
  controller: class AnalyzePublishedListController {
    constructor(AnalyzeService, $state, $window) {
      'ngInject';
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$window = $window;
    }

    $onInit() {
      this.loadAnalysis();
    }

    loadAnalysis() {
      this._AnalyzeService.getPublishedAnalysesByAnalysisId(this._$state.params.analysisId)
        .then(analyses => {
          this.analyses = analyses;
          console.log(analyses);
        });
    }

    edit(analysis) {
      this._$state.go('analyze.publishedDetail',
        {
          publishId: analysis.PUBLISHED_ANALYSIS_ID
        }
      );
    }

    getGridConfig() {
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
        caption: '',
        width: '30%',
        cellTemplate: 'actionCellTemplate'
      }];

      return {
        columns,
        bindingOptions: {
          dataSource: '$ctrl.analyses'
        },
        columnAutoWidth: true,
        allowColumnReordering: true,
        allowColumnResizing: true,
        showColumnHeaders: true,
        showColumnLines: false,
        showRowLines: false,
        showBorders: false,
        rowAlternationEnabled: true,
        hoverStateEnabled: true,
        scrolling: {
          mode: 'virtual'
        },
        sorting: {
          mode: 'multiple'
        },
        paging: {
          pageSize: 10
        },
        pager: {
          showPageSizeSelector: true,
          showInfo: true
        }
      };
    }

  }
};
