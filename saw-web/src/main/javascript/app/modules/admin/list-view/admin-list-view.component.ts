import { Component, Input } from '@angular/core';
import {dxDataGridService} from '../../../common/services/dxDataGrid.service';

const template = require('./admin-list-view.component.html');
require('./admin-list-view.component.scss');

@Component({
  selector: 'admin-list-view',
  template
})

export class AdminListViewComponent {

  @Input() data: any[];
  @Input() columns: any[];
  @Input() section: 'user' | 'role' | 'privilege' | 'categories';
  @Input() highlightTerm: string;

  config: any;

  constructor(
    private _dxDataGridService: dxDataGridService
  ) { }

  ngOnInit() {
    console.log('data', this.data);
    console.log('columns', this.columns);
    console.log('section', this.section);
    this.config = this.getConfig();
  }

  openDeleteModal(row) {
    console.log('del', row);
  }

  openEditModal(row) {
    console.log('row', row);
  }

  getConfig() {
    return this._dxDataGridService.mergeWithDefaultConfig({
      columns: this.columns,
      width: '100%',
      height: '100%'
      // customizeColumns: columns => {
      //   const last = columns.length - 1;
      // }
    });
  }
}
