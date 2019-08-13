import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { isUnique } from 'src/app/common/validators';
import * as isNil from 'lodash/isNil';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import {
  DetailFormable,
  CHANNEL_OPERATION,
  SFTPChannelMetadata,
  CHANNEL_ACCESS
} from 'src/app/modules/workbench/models/workbench.interface';

@Component({
  selector: 'sftp-source',
  templateUrl: './sftp-source.component.html',
  styleUrls: ['./sftp-source.component.scss']
})
export class SftpSourceComponent implements OnInit, DetailFormable {
  public detailsFormGroup: FormGroup;
  show = false;
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
      password: ['', Validators.required],
      description: [''],
      accessType: ['R', Validators.required]
    });
  }

  get value(): SFTPChannelMetadata {
    return this.detailsFormGroup.value;
  }

  get valid(): boolean {
    return this.detailsFormGroup.valid;
  }

  get testConnectivityValue() {
    const val = this.value;
    return {
      hostName: val.hostName,
      password: val.password,
      portNo: val.portNo,
      userName: val.userName
    };
  }

  togglePWD() {
    this.show = !this.show;
  }
}
