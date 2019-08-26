import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup
} from '@angular/forms';
import { RtisService } from './../../../services/rtis.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

@Component({
  selector: 'new-registration-form',
  templateUrl: './new-form-registration.component.html',
  styleUrls: ['./new-form-registration.component.scss']
})
export class NewRegistrationComponent implements OnInit {
  public detailsFormGroup: FormGroup;
  public primaryStream: {
    topic: '',
    queue: ''
  };
  constructor(
    private _formBuilder: FormBuilder,
    private _rtisService: RtisService,
    public notify: ToastService
  ) {}

  ngOnInit() {
    this.detailsFormGroup = this._formBuilder.group({
      primaryStreamTopic: [''],
      primaryStreamQueue: [''],
      secondaryStreamTopic: [''],
      secondaryStreamQueue: [''],
      handlerClass: [''],
      bootstrapServers: [''],
      batchSize: [],
      serializerKey: [],
      serializerValue: [],
      bufferFullSize: [],
      timeout: []
    });
  }

  createRegistration(data) {
    const requestBody = {
      app_key: Math.random().toString(36).substring(7),
      streams_1: [{
         topic: data.primaryStreamTopic,
         queue: data. primaryStreamQueue
       }],
      streams_2: [{
         topic: data.secondaryStreamTopic,
         queue: data.secondaryStreamQueue
       }],
       class: data.handlerClass,
       bootstrapServers: data.bootstrapServers,
       batchSize: data.batchSize,
       keySerializer: data.serializerKey,
       valueSerializer: data.serializerValue,
       blockOnBufferFull: data.bufferFullSize,
       timeoutMs: data.timeout
    };
    const response = this._rtisService.createRegistration(requestBody);
    this.notify.info(response.message, '', {
      hideDelay: 9000
    });
  }
}
