import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import * as isEmpty from 'lodash/isEmpty';
import { DatasourceService } from '../../../services/datasource.service';

import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';
import * as moment from 'moment';
import { CHANNEL_UID } from '../../../wb-comp-configs';
import {
  ROUTE_OPERATION,
  DetailForm
} from '../../../models/workbench.interface';

@Component({
  selector: 'create-route-dialog',
  templateUrl: './create-route-dialog.component.html',
  styleUrls: ['./create-route-dialog.component.scss']
})
export class CreateRouteDialogComponent {
  stepControl: FormGroup;
  uid = CHANNEL_UID;
  crondetails: any = {};
  opType: ROUTE_OPERATION = ROUTE_OPERATION.CREATE;
  channelName = '';
  isCronExpressionValid = false;
  startDateCorrectFlag = true;

  // All route forms implement this interface to guarantee common properties
  @ViewChild('sftpForm') sftpForm: DetailForm;
  @ViewChild('apiForm') apiForm: DetailForm;

  constructor(
    private dialogRef: MatDialogRef<CreateRouteDialogComponent>,
    private snackBar: MatSnackBar,
    private datasourceService: DatasourceService,
    private formBuilder: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public routeData: any
  ) {
    this.channelName = this.routeData.channelName;
    if (isUndefined(this.routeData.routeMetadata.length)) {
      this.opType = ROUTE_OPERATION.UPDATE;
    }
    if (isUndefined(this.routeData.routeMetadata.length)) {
      this.crondetails = this.routeData.routeMetadata.schedulerExpression;
    }

    this.createForm();
  }

  createForm() {
    this.stepControl = this.formBuilder.group({});
  }

  get routeDetails(): DetailForm {
    switch (this.routeData.channelType) {
      case CHANNEL_UID.SFTP:
        return this.sftpForm;
      case CHANNEL_UID.API:
        return this.apiForm;
      default:
        break;
    }
  }

  get isDetailsFormValid() {
    return this.routeDetails && this.routeDetails.valid;
  }

  get detailsFormValue() {
    return this.routeDetails && this.routeDetails.value;
  }

  get detailsFormTestValue() {
    return this.routeDetails && this.routeDetails.testConnectivityValue;
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  testRoute(routeInfo) {
    this.datasourceService.testRouteWithBody(routeInfo).subscribe(data => {
      this.showConnectivityLog(data);
    });
  }

  showConnectivityLog(logData) {
    this.dialogRef.updatePosition({ top: '30px' });
    const snackBarRef = this.snackBar.openFromComponent(
      TestConnectivityComponent,
      {
        data: logData,
        horizontalPosition: 'center',
        panelClass: ['mat-elevation-z9', 'testConnectivityClass']
      }
    );

    snackBarRef.afterDismissed().subscribe(() => {
      this.dialogRef.updatePosition({ top: '' });
    });
  }

  onCronChanged(cronexpression) {
    this.crondetails = cronexpression;
    this.isCronExpressionValid = !(
      isEmpty(cronexpression.cronexp) &&
      cronexpression.activeTab !== 'immediate'
    );
  }

  createRoute(data) {
    this.startDateCorrectFlag =
      this.crondetails.activeTab === 'immediate' ||
      moment(this.crondetails.startDate) > moment();
    if (!this.startDateCorrectFlag) {
      return false;
    }
    const routeDetails = this.mapData(data);
    this.dialogRef.close({ routeDetails, opType: this.opType });
  }

  mapData(data) {
    const routeDetails = {
      ...this.detailsFormValue,
      schedulerExpression: this.crondetails
    };
    return routeDetails;
  }
}
