import * as template from './analyze-executed-list.component.html';
import * as moment from 'moment';

export const AnalyzeExecutedListComponent = {
  template,
  bindings: {
    analysis: '<',
    analyses: '<',
    onSelect: '&'
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
        caption: 'TYPE',
        dataField: 'executionType',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      }, {
        caption: 'DATE',
        dataField: 'finished',
        dataType: 'date',
        calculateCellValue: rowData => {
          return moment.utc(rowData.finished).local().format('YYYY/MM/DD h:mm A');
        },
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      }, {
        caption: 'STATUS',
        dataField: 'status',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
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
      this.onSelect({executionId: executedAnalysis.id});
    }
  }
};
