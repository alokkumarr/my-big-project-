import { Component, OnInit, Input } from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  FormControl
} from '@angular/forms';
import { isUnique } from 'src/app/common/validators';
import * as isNil from 'lodash/isNil';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import {
  DetailForm,
  CHANNEL_OPERATION,
  HTTP_METHODS,
  APIChannelMetadata
} from 'src/app/modules/workbench/models/workbench.interface';
import { requireIf } from 'src/app/modules/observe/validators/required-if.validator';

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
      this.detailsFormGroup.patchValue(this.channelData);
    }
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
      hostAddress: ['', Validators.required],
      port: [null, [Validators.pattern('^[0-9]*$')]],
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

  get value(): APIChannelMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    return this.value;
  }
}
