import { Component } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { JwtService } from '../../../../../login/services/jwt.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddSecurityDialogComponent } from './../add-security-dialog/add-security-dialog.component';
import { AddAttributeDialogComponent } from './../add-attribute-dialog/add-attribute-dialog.component';
import {dxDataGridService} from '../../../../common/services/dxDataGrid.service';

const template = require('./security-group.component.html');
require('./security-group.component.scss');


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
  selectedGroupDetails: any;

  constructor(
    private _router: Router,
    private _jwtService: JwtService,
    private _dialog: MatDialog,
    private _dxDataGridService: dxDataGridService
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
  }

  initialise() {
    const token = this._jwtService.getTokenObj();
    this.ticket = token.ticket;
    //const customerId = parseInt(this.ticket.custID, 10);
  }

  addPropperty(property) {
    this.openSecurityModal(property, 'create')
      .afterClosed()
      .subscribe(rows => {
        if (rows) {
          //this.setData(rows);
        }
      });
  }

  openSecurityModal(property, mode: 'edit' | 'create') {
    mode = 'create';
    const data = {
      property,
      mode
    };
    const component = this.getModalComponent(property, mode) as any;
    console.log(component);
    return this._dialog.open(component, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }

  getModalComponent(property, mode) {
    if (property === 'securityGroup') {
      switch (mode) {
      case 'create':
        return AddSecurityDialogComponent;
      }
    } else if (property === 'attribute') {
      switch (mode) {
      case 'create':
        return AddAttributeDialogComponent;
      }
    }
  }

  getConfig() {
    const columns = [{
      caption: 'Group Name',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '70%'
    },{
      caption: '',
      //dataField: 'analysis.name',
      allowSorting: true,
      alignment: 'left',
      width: '30%',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.selectedGroupDetails = row.data;
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
