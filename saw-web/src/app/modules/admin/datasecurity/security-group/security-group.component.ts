import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Router, NavigationEnd } from '@angular/router';
import { JwtService } from '../../../../common/services/jwt.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddSecurityDialogComponent } from './../add-security-dialog/add-security-dialog.component';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../userassignment.service';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import * as isEmpty from 'lodash/isEmpty';
import * as deepClone from 'lodash/cloneDeep';

const template = require('./security-group.component.html');
require('./security-group.component.scss');

@Component({
  selector: 'security-group',
  templateUrl: './security-group.component.html',
  styleUrls: ['./security-group.component.scss']
})

export class SecurityGroupComponent implements OnInit {
  listeners: Subscription[] = [];
  ticket: { custID: string; custCode: string; masterLoginId?: string };
  filterObj = {
    searchTerm: '',
    searchTermValue: ''
  };

  config: any;
  data: any;
  groupSelected: any;
  groupName: any;
  columnData: {};
  emptyState: boolean;
  addAttribute: boolean;

  constructor(
    private _router: Router,
    private _jwtService: JwtService,
    private _dialog: MatDialog,
    private _dxDataGridService: DxDataGridService,
    private _userAssignmentService: UserAssignmentService,
    private _localSearch: LocalSearchService,
    private _toastMessage: ToastService
  ) {
    const navigationListener = this._router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
        this.initialise();
      }
    });

    this.listeners.push(navigationListener);
  }

  ngOnInit() {
    this.config = this.getConfig();
    this.loadGroupGridWithData(this.groupSelected);
    this.emptyState = true;
  }

  initialise() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
  }

  loadGroupGridWithData(groupSelected) {
    this.groupSelected = {};
    this.addAttribute = true;
    this._userAssignmentService.getSecurityGroups().then(response => {
      this.data = response;
      if (this.data.length === 0) {
        this.emptyState = true;
      } else {
        this.emptyState = false;
        this.groupSelected =  (isEmpty(groupSelected)) ? this.data[0] : groupSelected;
      }
      this.addAttribute = (this.data.length === 0);
    });
  }

  addPropperty(property, mode: 'edit' | 'create') {
    if (mode === 'create') {
      this.columnData = {};
    }
    const data = {
      property,
      mode,
      groupSelected: this.groupSelected,
      ...this.columnData
    };
    const component = this.getModalComponent(property) as any;
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      if (result) {
        this.loadGroupGridWithData(this.groupSelected);
      }
    });
  }

  editGroupData(data) {
    this.columnData = data;
    this.addPropperty('securityGroup', 'edit');
  }

  deleteGroup(cellData) {
    const data = {
      title: `Are you sure you want to delete this group?`,
      content: `Group Name: ${cellData.securityGroupName}`,
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    };
    return this._dialog.open(DeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      const path = `auth/admin/security-groups/${cellData.secGroupSysId}`;
      if (result) {
        this._userAssignmentService.deleteGroupOrAttribute(path).then(response => {
          this.loadGroupGridWithData(this.groupSelected);
        });
      }
    });
  }

  getModalComponent(property) {
    switch (property) {
    case 'securityGroup' :
      return AddSecurityDialogComponent;
    case 'attribute' :
      return AddAttributeDialogComponent;
    }
  }

  applySearchFilter(value) {
    const USERGROUP_SEARCH_CONFIG = [
      { keyword: 'Group Name', fieldName: 'securityGroupName' }
    ];
    this.filterObj.searchTerm = value;
    const searchCriteria = this._localSearch.parseSearchTerm(
      this.filterObj.searchTerm
    ) as any;
    this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
    this._localSearch
      .doSearch(searchCriteria, this.data, USERGROUP_SEARCH_CONFIG)
      .then(
        (data: any[]) => {
          this.data = data;
        },
        err => {
          this._toastMessage.error(err.message);
        }
      );
  }

  getConfig() {
    const columns = [{
      caption: 'Group Name',
      dataField: 'securityGroupName',
      cellTemplate: 'toolTipCellTemplate',
      allowSorting: true,
      alignment: 'left',
      width: '60%'
    }, {
      caption: 'ID',
      dataField: 'secGroupSysId',
      width: '0%'
    }, {
      caption: '',
      allowSorting: true,
      alignment: 'left',
      width: '30%',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.groupSelected = row.data;
      },
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
