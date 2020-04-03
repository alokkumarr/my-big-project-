import { Component, Inject } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import {
  SIPSubscriber,
  SubscriberChannelType
} from '../models/subscriber.model';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

import * as isNil from 'lodash/isNil';
import { HeaderProgressService } from 'src/app/common/services';
import { SubscriberService } from '../subscriber.service';

@Component({
  selector: 'add-subscriber',
  templateUrl: './add-subscriber.component.html',
  styleUrls: ['./add-subscriber.component.scss']
})
export class AddSubscriberComponent {
  subscriberForm: FormGroup;
  subscriber: SIPSubscriber;
  action: 'edit' | 'add';
  channelTypes: string[] = Object.values(SubscriberChannelType);
  progress: boolean;

  constructor(
    private fg: FormBuilder,
    @Inject(MAT_DIALOG_DATA) data,
    private dialogRef: MatDialogRef<AddSubscriberComponent>,
    headerProgress: HeaderProgressService,
    private subscriberService: SubscriberService
  ) {
    headerProgress.subscribe(value => {
      this.progress = value;
    });
    this.subscriber = data.subscriber || {};
    this.action =
      this.subscriber && !isNil(this.subscriber.id) ? 'edit' : 'add';

    this.createForm();
  }

  createForm() {
    this.subscriberForm = this.fg.group({
      id: [this.subscriber.id],
      subscriberName: [this.subscriber.subscriberName, [Validators.required]],
      channelType: [this.subscriber.channelType || SubscriberChannelType.EMAIL],
      channelValue: [this.subscriber.channelValue]
    });

    this.updateChannelValueValidators(
      this.subscriberForm.get('channelType').value
    );

    this.subscriberForm
      .get('channelType')
      .valueChanges.subscribe(this.updateChannelValueValidators.bind(this));
  }

  /* Update validators on channel value when channel type changes.
     For eg: channel value should validate for emails if channel type is email
  */
  updateChannelValueValidators(channelType: SubscriberChannelType) {
    switch (channelType) {
      case SubscriberChannelType.EMAIL:
        this.subscriberForm
          .get('channelValue')
          .setValidators([Validators.email]);
        break;
      default:
        this.subscriberForm.get('channelValue').setValidators([]);
    }

    this.subscriberForm.get('channelValue').updateValueAndValidity();
  }

  onCancel() {
    this.dialogRef.close();
  }

  onSave() {
    const payload: SIPSubscriber = {
      ...this.subscriberForm.value,
      id: this.subscriber.id
    };
    this.subscriberService.saveSubscriber(payload).subscribe(subscriber => {
      this.dialogRef.close(subscriber);
    });
  }
}
