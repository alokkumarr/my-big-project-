import { Component, Input, EventEmitter, Output } from '@angular/core';
import * as range from 'lodash/range';

@Component({
  selector: 'cron-date-picker',
  templateUrl: './cron-date-picker.component.html',
  styleUrls: ['./cron-date-picker.component.scss']
})
export class CronDatePickerComponent {
  @Input()
  public model: any;
  @Output()
  onDateChange = new EventEmitter();

  selectOptions = {
    hours: range(1, 13),
    minutes: range(0, 60),
    hourTypes: ['AM', 'PM']
  };

  triggerChange() {
    this.onDateChange.emit(this.model);
  }
}
