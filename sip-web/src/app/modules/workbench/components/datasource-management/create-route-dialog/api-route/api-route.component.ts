import { Component, Input, OnInit } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  FormControl,
  FormArray
} from '@angular/forms';
import {
  ROUTE_OPERATION,
  DetailForm,
  HTTP_METHODS,
  APIRouteMetadata
} from 'src/app/modules/workbench/models/workbench.interface';
import { isUnique } from 'src/app/common/validators';

import * as isUndefined from 'lodash/isUndefined';

import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { requireIf } from 'src/app/modules/observe/validators/required-if.validator';
import { CHANNEL_UID } from 'src/app/modules/workbench/wb-comp-configs';
import { SourceFolderDialogComponent } from '../../select-folder-dialog';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'api-route',
  templateUrl: './api-route.component.html',
  styleUrls: ['./api-route.component.scss']
})
export class ApiRouteComponent implements OnInit, DetailForm {
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
      const routeMetadata = <APIRouteMetadata>this.routeData.routeMetadata;

      this.patchFormArray(routeMetadata.headerParameters, 'headerParameters');
      this.patchFormArray(routeMetadata.queryParameters, 'queryParameters');

      this.detailsFormGroup.patchValue(this.routeData.routeMetadata);
    }
  }

  /**
   * Adds form controls for headers and query params for existing data.
   *
   * @param {Array<any>} data
   * @param {string} formKey
   * @memberof ApiRouteComponent
   */
  patchFormArray(data: Array<any>, formKey: string) {
    const formArray = this.detailsFormGroup.get(formKey) as FormArray;
    data.forEach(row =>
      formArray.push(
        this.formBuilder.group({
          key: [row.key, Validators.required],
          value: [row.value, Validators.required]
        })
      )
    );
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
      destinationLocation: ['', Validators.required],
      description: [''],
      apiEndPoint: [''],
      httpMethod: [HTTP_METHODS.GET, Validators.required],
      bodyParameters: this.formBuilder.group({
        content: [
          '',
          requireIf(
            control => this.detailsFormGroup.get('httpMethod') as FormControl,
            method =>
              [
                HTTP_METHODS.POST,
                HTTP_METHODS.PATCH,
                HTTP_METHODS.PUT
              ].includes(method)
          )
        ]
      }),
      headerParameters: this.formBuilder.array([]),
      queryParameters: this.formBuilder.array([]),
      urlParameters: this.formBuilder.array([])
    });
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

  get value(): APIRouteMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    return {
      channelType: CHANNEL_UID.API,
      channelId: this.routeData.channelID,
      ...this.value
    };
  }
}
