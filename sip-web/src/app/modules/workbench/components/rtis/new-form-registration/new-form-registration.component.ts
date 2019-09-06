import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators
} from '@angular/forms';
import { Location } from '@angular/common';
import { RtisService } from './../../../services/rtis.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { Router } from '@angular/router';
import * as isEmpty from 'lodash/isEmpty';

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
  public showEventUrl: boolean;
  public model: any;
  constructor(
    private _formBuilder: FormBuilder,
    private _rtisService: RtisService,
    public notify: ToastService,
    private _location: Location,
    private router: Router
  ) {}

  ngOnInit() {
    const fetchAppKeys = this._rtisService.getAppKeys();
    fetchAppKeys.then(response => {
      this.showEventUrl = isEmpty(response);
    });
    this.detailsFormGroup = this._formBuilder.group({
      batchSize: ['', [Validators.required]],
      eventUrl: [''],
      bufferFullSize: [''],
      timeout: [''],
      streamQueue: [''],
      streamTopic: [''],
      secondaryStreamQueue: ['']
    });
  }

  gotoRTISPage(){
    this._location.back();
  }

  createRegistration(data) {
    const requestBody = {
      id: this.model,
      app_key: Math.random().toString(36).substring(7),
      streams_1: [{
         topic: data.streamTopic,
         queue: data. streamQueue
       }],
       eventUrl: data.eventUrl,
       batchSize: parseInt(data.batchSize),
       blockOnBufferFull: data.bufferFullSize,
       timeoutMs: data.timeout
    };
    const changeSchedule = this._rtisService.createRegistration(requestBody);
    changeSchedule.then(response => {
      this.notify.info(response.message, '', {
        hideDelay: 9000
      });
      this.router.navigate(['workbench', 'rtis', 'appkeys']);
    });

  }
}
