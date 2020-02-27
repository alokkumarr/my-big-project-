import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import * as moment from 'moment';
import * as get from 'lodash/get';

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
  @Output() selectExecution: EventEmitter<string> = new EventEmitter();

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
        caption: 'DATE',
        dataField: 'finished',
        dataType: 'date',
        calculateCellValue: rowData => {
          const timeStamp = rowData.finished || rowData.finishedTime;
          return timeStamp
            ? moment
                .utc(timeStamp)
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
            ? '<i class="icon-checkmark" e2e="load-execution" style="font-size: 16px; color: green; margin-left: 10px"></i>'
            : '<i class="icon-close" e2e="load-execution" style="font-size: 10px; color: red; margin-left: 10px"></i>'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.selectExecution.emit(get(row, 'data.id') || get(row, 'data.executionId'));
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
