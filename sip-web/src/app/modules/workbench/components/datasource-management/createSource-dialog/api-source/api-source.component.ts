import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray } from '@angular/forms';
import { isUnique } from 'src/app/common/validators';
import * as isNil from 'lodash/isNil';
import * as cloneDeep from 'lodash/cloneDeep';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import {
  DetailForm,
  CHANNEL_OPERATION,
  HTTP_METHODS,
  APIChannelMetadata
} from 'src/app/modules/workbench/models/workbench.interface';
import { HttpMetadataComponent } from '../http-metadata/http-metadata.component';

@Component({
  selector: 'api-source',
  templateUrl: './api-source.component.html',
  styleUrls: ['./api-source.component.scss']
})
export class ApiSourceComponent implements OnInit, DetailForm {
  public detailsFormGroup: FormGroup;
  httpMethods: HTTP_METHODS[] = [
    HTTP_METHODS.GET,
    HTTP_METHODS.POST,
    HTTP_METHODS.PUT,
    HTTP_METHODS.PATCH,
    HTTP_METHODS.DELETE
  ];

  @Input() channelData: any;
  @Input() opType: CHANNEL_OPERATION;

  constructor(
    private formBuilder: FormBuilder,
    private datasourceService: DatasourceService
  ) {}

  ngOnInit() {
    this.createForm();

    if (isNil(this.channelData.length)) {
      const {
        provisionalHeaders,
        headers
      } = HttpMetadataComponent.getInitialProvisionalHeaders(
        this.channelData.headerParameters
      );
      this.channelData.headerParameters = headers;
      this.patchFormArray(
        this.channelData.headerParameters || [],
        'headerParameters'
      );
      this.patchFormArray(
        this.channelData.queryParameters || [],
        'queryParameters'
      );

      this.patchFormArray(provisionalHeaders, 'provisionalHeaders');

      this.detailsFormGroup.patchValue(this.channelData);
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
    const oldChannelName =
      this.opType === 'update' ? this.channelData.channelName : '';
    this.detailsFormGroup = this.formBuilder.group({
      channelName: [
        '',
        Validators.required,
        isUnique(
          this.datasourceService.isDuplicateChannel,
          v => v,
          oldChannelName
        )
      ],
      hostName: ['', [Validators.required, Validators.pattern(/^https?:\/\//)]],
      portNo: [null, [Validators.pattern('^[0-9]*$')]],
      description: [''],
      apiEndPoint: [''],
      httpMethod: [HTTP_METHODS.GET, Validators.required],
      bodyParameters: this.formBuilder.group({
        content: ['']
      }),
      headerParameters: this.formBuilder.array([]),
      provisionalHeaders: this.formBuilder.array([]),
      queryParameters: this.formBuilder.array([]),
      urlParameters: this.formBuilder.array([])
    });
  }

  get value(): APIChannelMetadata {
    const formValue = cloneDeep(this.detailsFormGroup.value);
    formValue.headerParameters = formValue.headerParameters.concat(
      cloneDeep(formValue.provisionalHeaders)
    );
    delete formValue.provisionalHeaders;
    return formValue;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    return this.value;
  }
}
