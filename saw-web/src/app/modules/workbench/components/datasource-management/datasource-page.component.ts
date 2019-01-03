import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material';
import { MatSnackBar } from '@angular/material';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import { map as rxMap, finalize } from 'rxjs/operators';

import { CHANNEL_TYPES } from '../../wb-comp-configs';
import { ChannelObject } from '../../models/workbench.interface';

import { DatasourceService } from '../../services/datasource.service';
import { generateSchedule } from '../../../../common/utils/cron2Readable';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { CreateSourceDialogComponent } from './createSource-dialog/createSource-dialog.component';
import { CreateRouteDialogComponent } from './create-route-dialog/create-route-dialog.component';
import { TestConnectivityComponent } from './test-connectivity/test-connectivity.component';
import { ConfirmActionDialogComponent } from './confirm-action-dialog/confirm-action-dialog.component';
import { LogsDialogComponent } from './logs-dialog';
import * as isUndefined from 'lodash/isUndefined';
import * as forEach from 'lodash/forEach';
import * as countBy from 'lodash/countBy';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
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
  // channel activation/deactivation request is pending
  channelToggleRequestPending = false;
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
    this.datasourceService.getSourceList()
      .pipe(
        rxMap(channels => map(channels, channel => ({...channel, ...JSON.parse(channel.channelMetadata)})))
      ).subscribe(channels => {
        this.unFilteredSourceData = channels;
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
      const firstChannelId = this.sourceData[0].bisChannelSysId;
      if (this.selectedSourceData) {
        const selectedId = this.selectedSourceData.bisChannelSysId;
        const alreadySelected = find(this.sourceData, ({bisChannelSysId}) => bisChannelSysId === selectedId);
        if (alreadySelected) {
          this.selectSingleChannel(selectedId);
        } else {
          this.selectSingleChannel(firstChannelId);
        }
      } else {
        this.selectSingleChannel(firstChannelId);
      }
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
      this.selectChannel(event.selectedRowsData[0]);
      if (!this.channelToggleRequestPending) {
        this.getRoutesForChannel(event.selectedRowKeys[0]);
      }
    } else if (event.selectedRowKeys.length > 0) {
      this.channelEditable = true;
      this.selectChannel(event.selectedRowsData[0]);
    } else {
      this.channelEditable = false;
      this.selectChannel(null);
      this.routesData = [];
    }
  }

  selectChannel(channel) {
    this.selectedSourceData = channel;
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
          payload.status = !channelData.status ? 0 : 1;

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
      width: '350px',
      data: {
        typeTitle: 'Channel Name',
        typeName: this.selectedSourceData.channelName,
        routesNr: this.routesData.length
      }
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
    this.datasourceService.getRoutesList(channelID)
      .pipe(
        rxMap(routes => map(routes, route => ({...route, ...JSON.parse(route.routeMetadata)})))
      )
      .subscribe(routes => {
        this.routesData = routes;
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
        channelID: this.selectedSourceData.bisChannelSysId,
        channelName: this.selectedSourceData.channelName
      }
    });

    dateDialogRef.afterClosed().subscribe(data => {
      if (!isUndefined(data)) {
        const payload: {status?: number, createdBy: string, routeMetadata: Object} = {
          createdBy: '',
          // Route metadata JSON object have to be stringified  to store in MariaDB due to BE limitation.
          routeMetadata: JSON.stringify(data.routeDetails)
        };
        if (data.opType === 'create') {
          this.datasourceService
            .createRoute(routeData, payload)
            .subscribe(createdRoute => {
              const promise = this.afterRouteAddedChanged(createdRoute);
              if (promise) {
                promise.then(() => {
                  this.getRoutesForChannel(routeData);
                });
              } else {
                this.getRoutesForChannel(routeData);
              }
            });
        } else {
          payload.createdBy = routeData.createdBy;
          payload.status = !routeData.status ? 0 : 1;

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

  afterRouteAddedChanged(createdRoute) {
    const channelId = createdRoute.bisChannelSysId;
    const routeId = createdRoute.bisRouteSysId;
    const selectedChannelId = this.selectedSourceData.bisChannelSysId;
    const isChannelNotActive = this.selectedSourceData.status === 0;
    if (isChannelNotActive && channelId === selectedChannelId) {
      return this.datasourceService.toggleRoute(channelId, routeId, false).toPromise();
    }
  }

  deleteRoute(routeData) {
    const dialogRef = this.dialog.open(ConfirmActionDialogComponent, {
      width: '350px',
      data: {
        typeTitle: 'Route Name',
        typeName: routeData.routeName
      }
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

  calculateScheduleCellValue(rowData) {
    const { cronexp, activeTab } = rowData.schedulerExpression;
    return generateSchedule(cronexp, activeTab);
  }

  togglePWD() {
    this.show = !this.show;
  }

  openLogsDialog(routeData) {
    this.dialog.open(LogsDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '590px',
      width: '800px',
      panelClass: 'sourceDialogClass',
      data: {
        ...routeData,
        channelName: this.selectedSourceData.channelName
      }
    });
  }

  toggleRouteActivation(route) {
    const { bisChannelSysId, bisRouteSysId, status } = route;
    this.datasourceService.toggleRoute(bisChannelSysId, bisRouteSysId, !status).subscribe(() => {
      route.status = this.reverseStatus(status);
    });
  }

  toggleChannelActivation(channel) {
    const { status } = channel;
    this.channelToggleRequestPending = true;
    this.datasourceService.toggleChannel(channel.bisChannelSysId, !status)
    .pipe(
      finalize(() => {
        this.channelToggleRequestPending = false;
      })
    ).subscribe(() => {
      this.channelToggleRequestPending = false;
      channel.status = this.reverseStatus(status);
      this.getRoutesForChannel(channel.bisChannelSysId);
    });
  }

  toggleAllRoutesOnFrontEnd(status) {
    forEach(this.routesData, route => {
      route.status = status;
    });
  }

  reverseStatus(status) {
    return status === 1 ? 0 : 1;
  }
}
