import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';

import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { Analysis } from '../../types';

@Component({
  selector: 'executed-list',
  templateUrl: './executed-list.component.html',
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
    public _DxDataGridService: DxDataGridService,
    public _router: Router
  ) {}

  goToExecution(executedAnalysis) {
    this._router.navigate(
      ['analyze', 'analysis', this.analysis.id, 'executed'],
      {
        queryParams: {
          executionId: executedAnalysis.id,
          awaitingExecution: false,
          loadLastExecution: false
        }
      }
    );
  }

  getGridConfig() {
    const columns = [
      {
        caption: 'ID',
        dataField: 'id',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      },
      {
        caption: 'TYPE',
        dataField: 'executionType',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      },
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
        width: '20%'
      },
      {
        caption: 'STATUS',
        dataField: 'status',
        allowSorting: true,
        alignment: 'left',
        width: '20%'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
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
