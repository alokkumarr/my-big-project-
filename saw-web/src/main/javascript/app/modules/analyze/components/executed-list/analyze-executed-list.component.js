import * as template from './analyze-executed-list.component.html';
import * as moment from 'moment';

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
        dataType: 'string',
        calculateCellValue: rowData => {
          let d = moment(rowData.finished).zone(new Date().getTimezoneOffset()).format('MM-DD-YYYY'); // The 0 there is the key, which sets the date to the epoch
          return (d || '');
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
