import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router, NavigationEnd } from '@angular/router';
import { JwtService } from '../../../../common/services/jwt.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddSecurityDialogComponent } from './../add-security-dialog/add-security-dialog.component';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { DataSecurityService } from './../datasecurity.service';
import { DskFiltersService } from './../../../../common/services/dsk-filters.service';
import { DeleteDialogComponent } from './../delete-dialog/delete-dialog.component';
import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';
import { DskFilterDialogComponent } from '../../../../common/dsk-filter-dialog/dsk-filter-dialog.component';
import { DSKFilterGroup } from '../../../../common/dsk-filter.model';
import { ConfirmDialogComponent } from 'src/app/common/components/confirm-dialog';

@Component({
  selector: 'security-group',
  templateUrl: './security-group.component.html',
  styleUrls: ['./security-group.component.scss']
})
export class SecurityGroupComponent implements OnInit {
  listeners: Subscription[] = [];
  ticket: { custID: string; custCode: string; masterLoginId?: string };
  config: any;
  data: any;
  groupSelected: any;
  groupName: any;
  groupFilters: DSKFilterGroup;
  columnData: {};
  emptyState: boolean;
  dskFiltersLoading = true;

  constructor(
    private _router: Router,
    private _jwtService: JwtService,
    private _dialog: MatDialog,
    private _dxDataGridService: DxDataGridService,
    private datasecurityService: DataSecurityService,
    private _DskFiltersService: DskFiltersService
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

  async onGroupSelected(group) {
    this.dskFiltersLoading = true;
    this.groupSelected = group;
    try {
      const filters = await this.datasecurityService
        .getFiltersFor(this.groupSelected.secGroupSysId)
        .toPromise();

      this.groupFilters =
        filters && !isEmpty(filters.booleanQuery) ? filters : null;

      this.dskFiltersLoading = false;
    } catch {
      this.groupFilters = null;
      this.dskFiltersLoading = false;
    }
  }

  loadGroupGridWithData(groupSelected) {
    this.dskFiltersLoading = true;
    this.datasecurityService.getSecurityGroups().then(response => {
      this.data = response;
      if (this.data.length === 0) {
        this.emptyState = true;
      } else {
        this.emptyState = false;
        isEmpty(groupSelected)
          ? this.onGroupSelected(this.data[0])
          : this.onGroupSelected(groupSelected);
      }
    });
  }

  openSecurityGroupDialog(property, mode: 'edit' | 'create') {
    if (mode === 'create') {
      this.columnData = {};
    }
    const data = {
      property,
      mode,
      groupSelected: this.groupSelected,
      ...this.columnData
    };
    return this._dialog
      .open(AddSecurityDialogComponent, {
        width: 'auto',
        height: 'auto',
        autoFocus: false,
        data
      } as MatDialogConfig)
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.loadGroupGridWithData({
            secGroupSysId: result.groupId,
            securityGroupName: result.groupName,
            description: result.description
          });
        }
      });
  }

  updateDskFilters() {
    const data = {
      groupSelected: this.groupSelected,
      filterGroup: cloneDeep(this.groupFilters)
    };
    return this._dialog
      .open(DskFilterDialogComponent, {
        width: 'auto',
        height: 'auto',
        autoFocus: false,
        data
      } as MatDialogConfig)
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this.loadGroupGridWithData(this.groupSelected);
        }
      });
  }

  deleteDskFilters() {
    this._dialog
      .open(ConfirmDialogComponent, {
        data: {
          positiveActionLabel: 'Delete',
          negativeActionLabel: 'Cancel',
          primaryColor: 'warn',
          title: 'Delete Filters',
          content: `Are you sure you want to delete all filters from group: ${this.groupSelected.securityGroupName}?`
        }
      })
      .afterClosed()
      .subscribe(result => {
        if (result) {
          this._DskFiltersService
            .deleteDskFiltersForGroup(this.groupSelected.secGroupSysId)
            .then(() => {
              this.onGroupSelected(this.groupSelected);
            });
        }
      });
  }

  editGroupData(data) {
    this.columnData = data;
    this.openSecurityGroupDialog('securityGroup', 'edit');
  }

  deleteGroup(cellData) {
    const data = {
      title: `Are you sure you want to delete this group?`,
      content: `Group Name: ${cellData.securityGroupName}`,
      positiveActionLabel: 'Delete',
      negativeActionLabel: 'Cancel'
    };
    return this._dialog
      .open(DeleteDialogComponent, {
        width: 'auto',
        height: 'auto',
        autoFocus: false,
        data
      } as MatDialogConfig)
      .afterClosed()
      .subscribe(result => {
        const path = `auth/admin/security-groups/${cellData.secGroupSysId}`;
        if (result) {
          this.datasecurityService
            .deleteGroupOrAttribute(path)
            .then(response => {
              this.loadGroupGridWithData({});
            });
        }
      });
  }

  getConfig() {
    const columns = [
      {
        caption: 'Group Name',
        dataField: 'securityGroupName',
        cellTemplate: 'toolTipCellTemplate',
        allowSorting: true,
        alignment: 'left',
        width: '60%'
      },
      {
        caption: 'ID',
        dataField: 'secGroupSysId',
        width: '10%'
      },
      {
        caption: '',
        allowSorting: true,
        alignment: 'left',
        width: '30%',
        cellTemplate: 'actionCellTemplate'
      }
    ];
    return this._dxDataGridService.mergeWithDefaultConfig({
      onRowClick: row => {
        this.onGroupSelected(row.data);
      },
      columns,
      columnMinWidth: 50,
      rowAlternationEnabled: false,
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
