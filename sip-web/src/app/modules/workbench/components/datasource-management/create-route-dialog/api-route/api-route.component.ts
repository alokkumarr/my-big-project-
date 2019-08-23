import { Component, Input, OnInit } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  FormControl
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
    private datasourceService: DatasourceService
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
      destinationLocation: ['', Validators.required],
      description: [''],
      apiEndPoint: ['', Validators.required],
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

  get value(): APIRouteMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    return {
      channelType: 'sftp',
      channelId: this.routeData.channelID,
      ...this.value
    };
  }
}
