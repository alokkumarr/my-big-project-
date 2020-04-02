import { Component, OnInit } from '@angular/core';
import { DxDataGridService } from './../../../../../common/services/dxDataGrid.service';
import { Router } from '@angular/router';
import { RtisService } from './../../../services/rtis.service';
import { DeleteDialogComponent } from './../../../../../common/components/delete-dialog/delete-dialog.component';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import * as isEmpty from 'lodash/isEmpty';
import * as find from 'lodash/find';

@Component({
  selector: 'appkeys-view',
  templateUrl: './appkeys-view.component.html',
  styleUrls: ['./appkeys-view.component.scss']
})
export class AppkeysViewComponent implements OnInit {
  public config: [];
  public appKeys: [];
  public custEventUrl: string;
  constructor(
    public _DxDataGridService: DxDataGridService,
    private router: Router,
    private _rtisService: RtisService,
    private _dialog: MatDialog,
    public notify: ToastService
  ) {}

  ngOnInit() {
    this.config = this.getGridConfig();
    this.fetchKeysForGrid();
  }

  navigate() {
    this.router.navigate(['workbench', 'rtis', 'registration']);
  }

  fetchKeysForGrid() {
    const fetchAppKeys = this._rtisService.getAppKeys();
    fetchAppKeys.then(response => {
      // Need to Pick the first available event url in the appkey array of
      // objects to display in list of all appkeys screen.
      this.custEventUrl = isEmpty(response)
        ? ''
        : find(response || response[0], 'eventUrl');
      this.appKeys = response;
    });
  }

  deleteAppKey(appKeyData) {
    const data = {
      title: `Are you sure you want to delete this App Key?`,
      content: `App Key: ${appKeyData.app_key}`,
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
        if (result) {
          const DeleteAppKeys = this._rtisService.deleteAppKey(
            appKeyData.app_key
          );
          DeleteAppKeys.then(response => {
            if (response) {
              this.fetchKeysForGrid();
              this.notify.info('App Key Deleted Successfully', '', {
                hideDelay: 9000
              });
            }
          });
        }
      });
  }

  getGridConfig() {
    const columns = [
      {
        caption: 'APP KEYS',
        dataField: 'app_key',
        width: '70%',
        height: '30'
      },
      {
        caption: 'ACTIONS',
        cellTemplate: 'actionCellTemplate',
        width: '30%',
        height: '30'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
      columns,
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      },
      width: '100%',
      height: '100%'
    });
  }
}
