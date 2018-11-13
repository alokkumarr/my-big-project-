import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material';
import { MatSnackBar } from '@angular/material';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';

import { CHANNEL_TYPES } from '../../wb-comp-configs';
import { ChannelObject } from '../../models/workbench.interface';

import { DatasourceService } from '../../services/datasource.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { CreateSourceDialogComponent } from './createSource-dialog/createSource-dialog.component';
import { CreateRouteDialogComponent } from './create-route-dialog/create-route-dialog.component';
import { TestConnectivityComponent } from './test-connectivity/test-connectivity.component';
import { ConfirmActionDialogComponent } from './confirm-action-dialog/confirm-action-dialog.component';
import * as isUndefined from 'lodash/isUndefined';
import * as forEach from 'lodash/forEach';
import * as countBy from 'lodash/countBy';
import * as get from 'lodash/get';
import * as merge from 'lodash/merge';
import * as findKey from 'lodash/findKey';
import * as filter from 'lodash/filter';

/* NOTE: In the below channel and source are synonyms and refer to a single connection to a host. */
@Component({
  selector: 'datasource-page',
  templateUrl: './datasource-page.component.html',
  styleUrls: ['./datasource-page.component.scss']
})
export class DatasourceComponent implements OnInit, OnDestroy {
  unFilteredSourceData = [];
  routesData = [];
  sourceData = [];
  sourceTypes = CHANNEL_TYPES;
  selectedSourceType: string;
  selectedSourceData: any;
  show = false;
  channelEditable = false;

  @ViewChild('channelsGrid')
  channelsGrid: DxDataGridComponent;

  constructor(
    public dialog: MatDialog,
    private datasourceService: DatasourceService,
    private notify: ToastService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.getSources();
  }

  ngOnDestroy() {}

  getSources() {
    this.unFilteredSourceData = [];
    this.datasourceService.getSourceList().subscribe(data => {
      forEach(data.content, value => {
        // Channel metadata is stored as stringified JSON due to BE limitation. So have to Parse it back.
        const tempVar = merge(value, JSON.parse(value.channelMetadata));
        this.unFilteredSourceData.push(tempVar);
      });
      this.countSourceByType(this.unFilteredSourceData);
    });
  }

  countSourceByType(sources) {
    const sCountObj = countBy(sources, 'channelType');
    this.selectedSourceType = findKey(sCountObj);
    this.filterSourcesByType(sources, this.selectedSourceType);
    forEach(this.sourceTypes, value => {
      const count = get(sCountObj, value.uid);
      value.count = isUndefined(count) ? 0 : count;
      value.color = value.count > 0 ? 'primary' : 'warn';
    });
  }

  filterSourcesByType(channelData, cType) {
    this.sourceData = filter(channelData, ['channelType', cType]);
    if (this.sourceData.length > 0) {
      this.selectSingleChannel(this.sourceData[0].bisChannelSysId);
    }
  }

  selectSingleChannel(channelID) {
    setTimeout(() => {
      this.channelsGrid.instance.deselectAll();
      this.channelsGrid.instance.selectRows([channelID], false);
      this.getRoutesForChannel(channelID);
    });
  }

  onSourceSelectionChanged(event) {
    if (
      !isUndefined(event.currentDeselectedRowKeys[0]) &&
      event.selectedRowKeys.length > 0
    ) {
      this.channelEditable = true;
      this.selectedSourceData = event.selectedRowsData[0];
      this.getRoutesForChannel(event.selectedRowKeys[0]);
    } else if (event.selectedRowKeys.length > 0) {
      this.channelEditable = true;
      this.selectedSourceData = event.selectedRowsData[0];
    } else {
      this.channelEditable = false;
      this.selectedSourceData = [];
      this.routesData = [];
    }
  }

  sourceSelectedType(sourceType, channelCount) {
    if (channelCount > 0) {
      this.filterSourcesByType(this.unFilteredSourceData, sourceType);
      this.selectedSourceType = sourceType;
    }
  }

  createSource(channelData) {
    const channelMetadata = isUndefined(channelData)
      ? []
      : JSON.parse(channelData.channelMetadata);
    const dateDialogRef = this.dialog.open(CreateSourceDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '60%',
      width: '70%',
      minWidth: '750px',
      minHeight: '600px',
      maxWidth: '900px',
      panelClass: 'sourceDialogClass',
      data: channelMetadata
    });

    dateDialogRef.afterClosed().subscribe(data => {
      if (!isUndefined(data)) {
        const payload: ChannelObject = {
          createdBy: '',
          productCode: '',
          projectCode: '',
          customerCode: '',
          channelType: data.sourceDetails.channelType,
          // Channel metadata JSON object have to be stringified  to store in MariaDB due to BE limitation.
          channelMetadata: JSON.stringify(data.sourceDetails)
        };
        if (data.opType === 'create') {
          this.datasourceService.createSource(payload).subscribe(() => {
            this.getSources();
          });
        } else {
          payload.createdBy = channelData.createdBy;
          payload.productCode = channelData.productCode;
          payload.projectCode = channelData.projectCode;
          payload.customerCode = channelData.customerCode;

          this.datasourceService
            .updateSource(channelData.bisChannelSysId, payload)
            .subscribe(() => {
              this.getSources();
            });
        }
      }
    });
  }

  deleteChannel(channelID) {
    const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
      width: '350px'
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.datasourceService.deleteChannel(channelID).subscribe(() => {
          this.notify.success('Channel deleted successfully');
          this.getSources();
        });
      }
    });
  }

  testChannel(channelID) {
    this.datasourceService.testChannel(channelID).subscribe(data => {
      this.showConnectivityLog(data);
    });
  }

  testRoute(routeData) {
    const routeID = routeData.bisRouteSysId;
    this.datasourceService.testRoute(routeID).subscribe(data => {
      this.showConnectivityLog(data);
    });
  }

  showConnectivityLog(logData) {
    this.snackBar.openFromComponent(TestConnectivityComponent, {
      data: logData,
      horizontalPosition: 'center',
      panelClass: ['mat-elevation-z9', 'testConnectivityClass']
    });
  }

  onToolbarPreparing(e) {
    e.toolbarOptions.items.unshift({
      location: 'before',
      template: 'sourceTypeTemplate'
    });
  }

  onRoutesToolbarPreparing(e) {
    e.toolbarOptions.items.unshift({
      location: 'before',
      template: 'nameTemplate'
    });
  }

  getRoutesForChannel(channelID) {
    this.routesData = [];
    this.datasourceService.getRoutesList(channelID).subscribe(data => {
      forEach(data.content, value => {
        // routes metadata is stored as stringified JSON due to BE limitation. So have to Parse it back.
        const tempVar = merge(value, JSON.parse(value.routeMetadata));
        this.routesData.push(tempVar);
      });
    });
  }

  createRoute(routeData) {
    const routeMetadata = isUndefined(routeData.routeMetadata)
      ? []
      : JSON.parse(routeData.routeMetadata);
    const dateDialogRef = this.dialog.open(CreateRouteDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '60%',
      width: '70%',
      minWidth: '750px',
      minHeight: '600px',
      maxWidth: '900px',
      panelClass: 'sourceDialogClass',
      data: {
        routeMetadata,
        channelID: this.selectedSourceData.bisChannelSysId
      }
    });

    dateDialogRef.afterClosed().subscribe(data => {
      if (!isUndefined(data)) {
        const payload = {
          createdBy: '',
          // Route metadata JSON object have to be stringified  to store in MariaDB due to BE limitation.
          routeMetadata: JSON.stringify(data.routeDetails)
        };
        if (data.opType === 'create') {
          this.datasourceService
            .createRoute(routeData, payload)
            .subscribe(() => {
              this.getRoutesForChannel(routeData);
            });
        } else {
          payload.createdBy = routeData.createdBy;

          this.datasourceService
            .updateRoute(
              routeData.bisChannelSysId,
              routeData.bisRouteSysId,
              payload
            )
            .subscribe(() => {
              this.getRoutesForChannel(routeData.bisChannelSysId);
            });
        }
      }
    });
  }

  deleteRoute(routeData) {
    const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
      width: '350px'
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        const channelID = routeData.bisChannelSysId;
        const routeID = routeData.bisRouteSysId;

        this.datasourceService.deleteRoute(channelID, routeID).subscribe(() => {
          this.getRoutesForChannel(channelID);
        });
      }
    });
  }

  togglePWD() {
    this.show = !this.show;
  }
}
