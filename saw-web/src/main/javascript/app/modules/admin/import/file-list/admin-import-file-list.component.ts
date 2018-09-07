import { Component, Input, Output, EventEmitter } from '@angular/core';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./admin-import-file-list.component.html');
require('./admin-import-file-list.component.scss');

@Component({
  selector: 'admin-import-file-list',
  template
})

export class AdminImportFileListComponent {

  @Input() files: any[];
  @Output() remove = new EventEmitter<string>();

  config: any;

  constructor(
    private _dxDataGridService: dxDataGridService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
  }

  onRemove(row) {
    this.remove.emit(row.name);
  }

  getConfig() {
    const columns = [{
      caption: 'File Name',
      dataField: 'name',
      allowSorting: false,
      alignment: 'center',
      width: '50%'
    }, {
      caption: 'Analysis Count',
      dataField: 'count',
      allowSorting: true,
      alignment: 'left',
      width: '40%'
    }, {
      width: '10%',
      caption: '',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
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