import { Component, EventEmitter, Output } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { JwtService } from '../../../../../login/services/jwt.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddSecurityDialogComponent } from './../add-security-dialog/add-security-dialog.component';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';
import { UserAssignmentService } from './../userassignment.service';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';

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

  constructor(
    private _router: Router,
    private _jwtService: JwtService,
    private _dialog: MatDialog,
    private _dxDataGridService: dxDataGridService,
    private _userAssignmentService: UserAssignmentService
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
    this.loadGroupGridWithData();
  }

  initialise() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    //const customerId = parseInt(this.ticket.custID, 10);
  }

  loadGroupGridWithData() {
    this.data = [
      {
        securityGroupName: "sampleOne",
        description: "jkanskjjasdn"
      }, {
        securityGroupName: "sample5ne",
        description: "jkanskjjasdn"
      }, {
        securityGroupName: "sampletne",
        description: "jkanskjjasdn"
      }
    ];
    this.groupSelected = this.data[0].securityGroupName;
    // this._userAssignmentService.getSecurityGroups().then(response => {
    //   console.log(response);
    //   //this.data = response;
    //   this.data = [
    //     {
    //       securityGroupName: "sampleOne",
    //       description: "jkanskjjasdn"
    //     }, {
    //       securityGroupName: "sample5ne",
    //       description: "jkanskjjasdn"
    //     }, {
    //       securityGroupName: "sampletne",
    //       description: "jkanskjjasdn"
    //     }
    //   ];
    //   console.log(this.data);
    //   this.groupSelected = this.data[0].securityGroupName;
    // });
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
    console.log(data);
    const component = this.getModalComponent(property) as any;
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result) => {
      if (result) {
        this.loadGroupGridWithData();
      }
    });
  }

  editGroupData(data) {
    console.log(data);
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
    console.log(data);
    return this._dialog.open(DeleteDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig)
  }

  getModalComponent(property) {
    switch (property) {
    case 'securityGroup' :
      return AddSecurityDialogComponent;
    case 'attribute' :
      return AddAttributeDialogComponent;
    }
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
