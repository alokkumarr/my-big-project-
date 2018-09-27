import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as some from 'lodash/some';
import * as every from 'lodash/every';
import {DxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./admin-export-list.component.html');

@Component({
  selector: 'admin-export-list',
  template
})

export class AdminExportListComponent {

  @Input() analyses: any[];
  @Output() validityChange: EventEmitter<boolean> = new EventEmitter();

  config: any;

  areAllSelected = false;

  constructor(
    private _DxDataGridService: DxDataGridService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
  }

  onChecked(analysis) {
    analysis.selection = !analysis.selection;
    const isValid = some(this.analyses, 'selection');
    if (analysis.selection) {
      this.areAllSelected = every(this.analyses, 'selection');
    } else {
      this.areAllSelected = false;
    }
    this.validityChange.emit(isValid);
  }

  selectAll() {
    this.areAllSelected = !this.areAllSelected;
    forEach(this.analyses, analysis => {
      analysis.selection = this.areAllSelected;
    });
    this.validityChange.emit(this.areAllSelected);
  }

  getConfig() {
    const columns = [{
      caption: 'Select All to Export',
      dataField: 'selection',
      allowSorting: false,
      alignment: 'center',
      width: '5%',
      headerCellTemplate: 'selectionHeaderCellTemplate',
      cellTemplate: 'selectionCellTemplate'
    }, {
      caption: 'Analysis Name',
      dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '40%'
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
      width: '25%'
    }, {
      caption: 'Sub-Category Name',
      dataField: 'categoryName',
      allowSorting: true,
      alignment: 'left',
      width: '15%'
    }];
    return this._DxDataGridService.mergeWithDefaultConfig({
      columns,
      width: '100%',
      height: '100%',
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
