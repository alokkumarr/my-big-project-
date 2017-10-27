import * as template from './analyze-executed-list.component.html';

export const AnalyzeExecutedListComponent = {
  template,
  bindings: {
    analysis: '<',
    analyses: '<'
  },
  controller: class AnalyzeExecutedListController {
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
        calculateCellValue: rowData => {
          var _date = new Date(rowData.finished); 
          return (_date.toLocaleString('en-US', { timeZone: 'Europe/Helsinki' }));
        },
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
          this.goToExecution(row.data);
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

    goToExecution(executedAnalysis) {
      this._$state.go('analyze.executedDetail', {
        executionId: executedAnalysis.id,
        analysisId: this.analysis.id,
        analysis: this.analysis
      });
    }
  }
};
