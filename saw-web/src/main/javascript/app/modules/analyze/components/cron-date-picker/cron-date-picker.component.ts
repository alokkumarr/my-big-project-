declare const require: any;

import { Component, Inject, Input } from '@angular/core';
import { model } from '../cron-job-schedular.component';
const template = require('./cron-date-picker.component.html');

@Component({
  selector: 'cron-date-picker',
  template
})
export class CronDatePickerComponent {

  @Input() public use24HourTime: boolean;
  @Input() public model: any;

  ngOnInit() {
  	console.log(this.model);
    this.selectOptions = {
      minutes: this.range(0, 59),
      seconds: this.range(0, 59),
      hourTypes: ['AM', 'PM']
    };

    this.selectOptions.hours = this.use24HourTime ? this.range(0, 23) : this.range(0, 12);
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }
}

