import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as some from 'lodash/some';
import * as every from 'lodash/every';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./admin-import-list.component.html');

@Component({
  selector: 'admin-import-list',
  template
})

export class AdminImportListComponent {

  @Input() analyses: any[];
  @Output() validityChange: EventEmitter<boolean> = new EventEmitter();

  config: any;

  areAllSelected = false;

  constructor(
    private _dxDataGridService: dxDataGridService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
  }

  overWrite(row) {
    row.selection = true;
    this.validityChange.emit(true);
  }

  onChecked(row) {
    row.selection = !row.selection;
    const isValid = some(this.analyses, row => !row.noMetricInd && row.selection);
    if (row.selection) {
      this.areAllSelected = every(this.analyses, row => !row.noMetricInd && row.selection);
    } else {
      this.areAllSelected = false;
    }
    this.validityChange.emit(isValid);
  }

  selectAll() {
    this.areAllSelected = !this.areAllSelected;
    forEach(this.analyses, row => {
      if (!row.noMetricInd) {
        row.selection = this.areAllSelected;
      }
    });
    this.validityChange.emit(this.areAllSelected);
  }

  getConfig() {
    const columns = [{
      caption: 'All',
      dataField: 'selection',
      allowSorting: false,
      alignment: 'left',
      width: '7%',
      headerCellTemplate: 'selectionHeaderCellTemplate',
      cellTemplate: 'selectionCellTemplate'
    }, {
      caption: 'Analysis Name',
      dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '30%'
    }, {
      caption: 'Analysis Type',
      dataField: 'analysis.type',
      allowSorting: true,
      alignment: 'left',
      width: '10%'
    }, {
      caption: 'Metric Name',
      dataField: 'analysis.metricName',
      allowSorting: true,
      alignment: 'left',
      width: '30%'
    }, {
      caption: 'Logs',
      dataField: 'log',
      allowSorting: false,
      alignment: 'left',
      width: '20%',
      cellTemplate: 'logCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      columns,
      scrolling: {
        mode: 'standard'
      },
      paging: {
        enabled: true,
        pageSize: 10,
        pageIndex: 0
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true,
        showNavigationButtons: true,
        allowedPageSizes: [5, 10, 20]
      }
    });
  }
}
