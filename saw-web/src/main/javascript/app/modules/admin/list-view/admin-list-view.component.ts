import { Component, Input, Output, EventEmitter } from '@angular/core';
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
  @Output() editRow: EventEmitter<any> = new EventEmitter();
  @Output() deleteRow: EventEmitter<any> = new EventEmitter();
  @Output() rowClick: EventEmitter<any> = new EventEmitter();

  securityGroups = [
    {value: 'group1', viewValue: 'Group 1'},
    {value: 'group2', viewValue: 'Group 2'},
    {value: 'group3', viewValue: 'Group 3'}
  ];

  config: any;

  constructor(
    private _dxDataGridService: dxDataGridService
  ) { }

  ngOnInit() {
    console.log(this.data);
    this.config = this.getConfig();
  }

  getLinkTooltip() {
    return 'View Privileges';
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
