import { Component, EventEmitter, Output } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { Router, NavigationEnd } from '@angular/router';
import { JwtService } from '../../../../../login/services/jwt.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddSecurityDialogComponent } from './../add-security-dialog/add-security-dialog.component';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../userassignment.service';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { ToastService } from '../../../../common/services/toastMessage.service';

const template = require('./security-group.component.html');
require('./security-group.component.scss');
let self;

@Component({
  selector: 'security-group',
  template
})

export class SecurityGroupComponent {
  listeners: Subscription[] = [];
  ticket: { custID: string; custCode: string; masterLoginId?: string };
  filterObj = {
    searchTerm: '',
    searchTermValue: ''
  };

  config: any;
  data: any;
  groupSelected: any = '';columnData: {};
  emptyState: boolean;

  constructor(
    private _router: Router,
    private _jwtService: JwtService,
    private _dialog: MatDialog,
    private _dxDataGridService: dxDataGridService,
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
    this._userAssignmentService.getSecurityGroups().then(response => {
      this.data = response;
      if (this.data.length === 0) {
        this.emptyState = true;
      } else {
        this.emptyState = false;
        this.groupSelected =  (groupSelected === '') ? this.data[0].securityGroupName : groupSelected;
      }
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
        this.groupSelected = '';
        this.loadGroupGridWithData(data.groupSelected);
      }
    });
  }

  editGroupData(data) {
    this.columnData = data;
    this.addPropperty('securityGroup','edit')
  }

  deleteGroup(cellData) {
    const data = {
      title: `Are you sure you want to delete this group?`,
      content: `Group Name: ${cellData.securityGroupName}`,
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    }
    return this._dialog.open(DeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      const path = `auth/deleteSecurityGroups?securityGroupName=${cellData.securityGroupName}`;
      if (result) {
        this._userAssignmentService.deleteGroupOrAttribute(path, '').then(response => {
          this.loadGroupGridWithData(this.groupSelected);
        })
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
      caption: '',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '30%',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.groupSelected = row.data.securityGroupName;
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
