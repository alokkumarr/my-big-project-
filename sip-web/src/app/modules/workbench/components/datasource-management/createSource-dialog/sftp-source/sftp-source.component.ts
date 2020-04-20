import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { isUnique } from 'src/app/common/validators';
import * as isNil from 'lodash/isNil';
import * as isEmpty from 'lodash/isEmpty';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import {
  DetailForm,
  CHANNEL_OPERATION,
  SFTPChannelMetadata,
  CHANNEL_ACCESS
} from 'src/app/modules/workbench/models/workbench.interface';

@Component({
  selector: 'sftp-source',
  templateUrl: './sftp-source.component.html',
  styleUrls: ['./sftp-source.component.scss']
})
export class SftpSourceComponent implements OnInit, DetailForm {
  public detailsFormGroup: FormGroup;
  accessTypes = CHANNEL_ACCESS;

  @Input() channelData: any;
  @Input() opType: CHANNEL_OPERATION;

  constructor(
    private formBuilder: FormBuilder,
    private datasourceService: DatasourceService
  ) {}

  ngOnInit() {
    this.createForm();
    if (isNil(this.channelData.length)) {
      this.channelData.password = '';
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
      hostName: ['', Validators.required],
      portNo: [
        '',
        Validators.compose([
          Validators.required,
          Validators.pattern('^[0-9]*$')
        ])
      ],
      userName: ['', Validators.required],
      password: [''],
      description: [''],
      accessType: ['R', Validators.required]
    });

    this.detailsFormGroup.get('password').setValidators(this.setRequired());
  }

  setRequired() {
    if(this.opType === 'create') {
      return [Validators.required];
    } else {
        return [];
    }
  }

  get value(): SFTPChannelMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    const val = this.value;
    const requestBody = {
      hostName: val.hostName,
      password: val.password,
      portNo: val.portNo,
      userName: val.userName
    }

    if (this.opType === 'update' && isEmpty(val.password)) {
      delete requestBody.password
    }
    return requestBody;
  }
}
