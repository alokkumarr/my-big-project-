import { Component, OnInit, Inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { MatSnackBar } from '@angular/material';
import * as isUndefined from 'lodash/isUndefined';
import * as includes from 'lodash/includes';
import { DatasourceService } from '../../../services/datasource.service';
import { isUnique } from '../../../../../common/validators';

import { SourceFolderDialogComponent } from '../select-folder-dialog';
import { TestConnectivityComponent } from '../test-connectivity/test-connectivity.component';

@Component({
  selector: 'create-route-dialog',
  templateUrl: './create-route-dialog.component.html',
  styleUrls: ['./create-route-dialog.component.scss']
})
export class CreateRouteDialogComponent implements OnInit {
  public detailsFormGroup: FormGroup;
  crondetails: any = {};
  opType = 'create';
  channelName = '';

  constructor(
    private _formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<CreateRouteDialogComponent>,
    private snackBar: MatSnackBar,
    private datasourceService: DatasourceService,
    private _dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public routeData: any
  ) {
    this.createForm();
  }

  createForm() {
    const channelId = this.routeData.channelID;
    const tranformerFn = value => ({channelId, routeName: value});
    this.detailsFormGroup = this._formBuilder.group({
      routeName: ['', Validators.required, isUnique(this.datasourceService.isDuplicateRoute, tranformerFn)],
      sourceLocation: ['', Validators.required],
      destinationLocation: ['', Validators.required],
      filePattern: ['', [Validators.required, this.validateFilePattern]],
      description: ['']
    });
  }

  ngOnInit() {
    this.channelName = this.routeData.channelName;
    if (isUndefined(this.routeData.routeMetadata.length)) {
      this.opType = 'update';
      this.detailsFormGroup.patchValue(this.routeData.routeMetadata);
      this.crondetails = this.routeData.routeMetadata.schedulerExpression;
    }
  }

  onCancelClick(): void {
    this.dialogRef.close();
  }

  validateFilePattern(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    if (includes(control.value, '.*') || includes(control.value, ',')) {
      return { inValidPattern: true };
    }
    return null;
  }

  testRoute(formData) {
    const routeInfo = {
      channelType: 'sftp',
      channelId: this.routeData.channelID,
      sourceLocation: formData.sourceLocation,
      destinationLocation: formData.destinationLocation
    };
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
  }

  createRoute(data) {
    const routeDetails = this.mapData(data);
    this.dialogRef.close({ routeDetails, opType: this.opType });
  }

  mapData(data) {
    const routeDetails = {
      routeName: data.routeName,
      sourceLocation: data.sourceLocation,
      destinationLocation: data.destinationLocation,
      filePattern: data.filePattern,
      schedulerExpression: this.crondetails,
      description: data.description
    };
    return routeDetails;
  }

  openSelectSourceFolderDialog() {
    const dateDialogRef = this._dialog.open(SourceFolderDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      height: '400px',
      width: '300px',
    });
    dateDialogRef.afterClosed().subscribe(sourcePath => {
      console.log('sourcePath', sourcePath);
      this.detailsFormGroup.controls.destinationLocation.setValue(sourcePath);
    });
  }
}
