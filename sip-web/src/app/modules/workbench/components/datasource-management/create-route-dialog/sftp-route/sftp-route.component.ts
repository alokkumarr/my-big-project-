import { Component, Input, OnInit } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  AbstractControl
} from '@angular/forms';
import {
  ROUTE_OPERATION,
  DetailForm,
  SFTPRouteMetadata
} from 'src/app/modules/workbench/models/workbench.interface';
import { isUnique } from 'src/app/common/validators';

import * as includes from 'lodash/includes';
import * as isUndefined from 'lodash/isUndefined';

import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { MatDialog } from '@angular/material';
import { SourceFolderDialogComponent } from '../../select-folder-dialog';

@Component({
  selector: 'sftp-route',
  templateUrl: './sftp-route.component.html',
  styleUrls: ['./sftp-route.component.scss']
})
export class SftpRouteComponent implements OnInit, DetailForm {
  @Input() routeData: any;
  @Input() opType: ROUTE_OPERATION;

  detailsFormGroup: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private datasourceService: DatasourceService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.createForm();
    if (isUndefined(this.routeData.routeMetadata.length)) {
      this.detailsFormGroup.patchValue(this.routeData.routeMetadata);
    }
  }

  createForm() {
    const channelId = this.routeData.channelID;
    const tranformerFn = value => ({ channelId, routeName: value });
    const oldRouteName =
      this.opType === ROUTE_OPERATION.UPDATE
        ? this.routeData.routeMetadata.routeName
        : '';
    this.detailsFormGroup = this.formBuilder.group({
      routeName: [
        '',
        Validators.required,
        isUnique(
          this.datasourceService.isDuplicateRoute,
          tranformerFn,
          oldRouteName
        )
      ],
      sourceLocation: ['', Validators.required],
      destinationLocation: ['', Validators.required],
      filePattern: ['', [Validators.required, this.validateFilePattern]],
      description: [''],
      disableDuplicate: [false],
      disableConcurrency: [false],
      batchSize: ['', [Validators.required]],
      fileExclusions: ['', this.validatefileExclusion],
      lastModifiedLimitHours: ['', Validators.pattern(/^\d*[1-9]\d*$/)]
    });
  }

  validateFilePattern(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    if (includes(control.value, ',')) {
      return { inValidPattern: true };
    }
    return null;
  }

  validatefileExclusion(
    control: AbstractControl
  ): { [key: string]: boolean } | null {
    if (includes(control.value, ',') || includes(control.value, '.')) {
      return { inValidPattern: true };
    }
    return null;
  }

  openSelectSourceFolderDialog() {
    const dateDialogRef = this.dialog.open(SourceFolderDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      height: '400px',
      width: '300px'
    });
    dateDialogRef.afterClosed().subscribe(sourcePath => {
      this.detailsFormGroup.controls.destinationLocation.setValue(sourcePath);
    });
  }

  get value(): SFTPRouteMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    return {
      channelType: 'sftp',
      channelId: this.routeData.channelID,
      sourceLocation: this.value.sourceLocation,
      destinationLocation: this.value.destinationLocation
    };
  }
}
