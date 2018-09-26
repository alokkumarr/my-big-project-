import { Component, Input, Output, EventEmitter } from '@angular/core';
import {DxDataGridService} from '../../../common/services/dxDataGrid.service';

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
  @Output() editRow: EventEmitter<any> = new EventEmitter();
  @Output() deleteRow: EventEmitter<any> = new EventEmitter();
  @Output() rowClick: EventEmitter<any> = new EventEmitter();

  config: any;

  constructor(
    private _DxDataGridService: DxDataGridService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
  }

  getLinkTooltip() {
    return 'View Privileges';
  }

  getConfig() {
    return this._DxDataGridService.mergeWithDefaultConfig({
      columns: this.columns,
      width: '100%',
      height: '100%'
      // customizeColumns: columns => {
      //   const last = columns.length - 1;
      // }
    });
  }
}
