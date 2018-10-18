import { Component, Input, Output, EventEmitter } from '@angular/core';
import {dxDataGridService} from '../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../datasecurity/userassignment.service';

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

  securityGroups= [];
  config: any;
  groupValue: any;
  groupAssignSuccess: any;
  userGroupID: any;

  constructor(
    private _dxDataGridService: dxDataGridService,
    private _userAssignmentService: UserAssignmentService
  ) { }

  ngOnInit() {
    this.config = this.getConfig();
    this._userAssignmentService.getSecurityGroups().then(response => {
      this.securityGroups = response;
    })
  }

  getLinkTooltip() {
    return 'View Privileges';
  }

  assignGrouptoUser(groupName, cell) {
    const request = {
      groupName : groupName.value,
      userId: cell.data.loginId
    }
    this._userAssignmentService.assignGroupToUser(request).then(response => {
      this.groupAssignSuccess = response.valid ? 'checkmark' : 'close';
      this.userGroupID = cell.data.loginId;
    });
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
