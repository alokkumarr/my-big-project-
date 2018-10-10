import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';

import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { Analysis } from '../../types';

const template = require('./executed-list.component.html');

@Component({
  selector: 'executed-list',
  template,
  styles: [
    `
      :host {
        display: block;
      }`
  ]
})
export class ExecutedListComponent {
  @Input('analyses')
  set setAnalyses(analyses: Analysis[]) {
    this.analyses = analyses;
    this.config = this.getGridConfig();
  }
  @Input() analysis: Analysis;

  config: any;
  analyses: Analysis[];

  constructor(
    private _dxDataGridService: dxDataGridService,
    private _router: Router
  ) {}

  goToExecution(executedAnalysis) {
    this._router.navigate(
      ['analyze', 'analysis', this.analysis.id, 'executed'],
      {
        queryParams: {
          executedAnalysis,
          awaitingExecution: false,
          loadLastExecution: false
        }
      }
    );
  }

  getGridConfig() {
    const columns = [
      {
        caption: 'DATE',
        dataField: 'finished',
        dataType: 'date',
        calculateCellValue: rowData => {
          return rowData.finished
            ? moment
                .utc(rowData.finished)
                .local()
                .format('YYYY/MM/DD h:mm A')
            : null;
        },
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      },
      {
        caption: 'TYPE',
        dataField: 'executionType',
        allowSorting: true,
        alignment: 'left',
        width: '30%'
      },
      {
        caption: 'STATUS',
        dataField: 'status',
        allowSorting: true,
        alignment: 'center',
        encodeHtml: false,
        width: '30%',
        calculateCellValue: data =>
          !data.status || data.status.toLowerCase() === 'success'
            ? '<i class="icon-checkmark" style="font-size: 16px; color: green; margin-left: 10px"></i>'
            : '<i class="icon-close" style="font-size: 10px; color: red; margin-left: 10px"></i>'
      }
    ];
    return this._dxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.goToExecution(row.data);
      },
      columns,
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
