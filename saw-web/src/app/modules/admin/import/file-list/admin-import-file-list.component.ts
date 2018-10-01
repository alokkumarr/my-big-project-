import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import {DxDataGridService} from '../../../../common/services/dxDataGrid.service';

const style = require('./admin-import-file-list.component.scss');

@Component({
  selector: 'admin-import-file-list',
  templateUrl: './admin-import-file-list.component.html',
  styles: [style]
})

export class AdminImportFileListComponent implements OnInit {

  @Input() files: any[];
  @Output() remove = new EventEmitter<string>();

  config: any;

  constructor(
    public _DxDataGridService: DxDataGridService
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
