import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as forEach from 'lodash/forEach';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./admin-export-list.component.html');
// require('./admin-export-list.component.scss');

@Component({
  selector: 'admin-export-list',
  template
})

export class AdminExportListComponent {

  @Input() analyses: any[];
  @Output() editRow: EventEmitter<any> = new EventEmitter();
  @Output() deleteRow: EventEmitter<any> = new EventEmitter();
  @Output() rowClick: EventEmitter<any> = new EventEmitter();

  config: any;

  areAllSelected = false;

  constructor(
    private _dxDataGridService: dxDataGridService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
  }

  onChecked(analysis) {
    analysis.selection = !analysis.selection;
    console.log('selection', analysis);
  }

  selectAll() {
    this.areAllSelected = !this.areAllSelected;
    console.log('areAllSelected', this.areAllSelected);
    forEach(this.analyses, analysis => {
      analysis.selection = this.areAllSelected;
    });
  }

  getConfig() {
    const columns = [{
      caption: 'Select All to Export',
      dataField: 'selection',
      allowSorting: false,
      alignment: 'center',
      width: '10%',
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
    return this._dxDataGridService.mergeWithDefaultConfig({
      // onInitialized: this.onGridInitialized.bind(this),
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
