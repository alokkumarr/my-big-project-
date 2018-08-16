import { Component, Input } from '@angular/core';
import { StateService } from '@uirouter/angular';
import * as forEach from 'lodash/forEach';
import * as moment from 'moment';

import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { Analysis } from '../../types';

const template = require('./executed-list.component.html');

@Component({
  selector: 'executed-list',
  template,
  styles: [`
      :host {
        display: block;
      }`]
})
export class ExecutedListComponent {
  @Input('analyses') set setAnalyses(analyses: Analysis[]) {
    this.analyses = analyses;
    this.config = this.getGridConfig();
  };
  @Input() analysis: Analysis;

  config: any;
  analyses: Analysis[];

  constructor(
    private _dxDataGridService: dxDataGridService,
    private _state: StateService
  ) {}

  goToExecution(executedAnalysis) {
    this._state.go('analyze.executedDetail', {
      executionId: executedAnalysis.id,
      analysisId: this.analysis.id,
      analysis: this.analysis,
      awaitingExecution: false,
      loadLastExecution: false
    });
  }

  getGridConfig() {
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
