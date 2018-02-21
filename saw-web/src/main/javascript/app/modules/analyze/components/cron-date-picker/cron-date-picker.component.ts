declare const require: any;

import { Component, Inject, Input, EventEmitter, Output } from '@angular/core';
import { model } from '../cron-job-schedular.component';
const template = require('./cron-date-picker.component.html');

@Component({
  selector: 'cron-date-picker',
  template
})
export class CronDatePickerComponent implements OnInit {

  @Input() public use24HourTime: boolean;
  @Input() public model: any;
  @Output() onDateChange = new EventEmitter();

  ngOnInit() {
    const timeArray = [];
    const d = new Date();
    const h = d.getHours();
    const m = d.getMinutes();
    this.selectOptions = {
      minutes: ['00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31','32','33','34','35','36','37','38','39','40','41','42','43','44','45','46','47','48','49','50','51','52','53','54','55','56','57','58','59'],
      hourTypes: ['AM', 'PM']
    };
    this.model.hourType = 'AM';
    this.selectOptions.hours = this.use24HourTime ? this.range(1, 23) : this.range(1, 12);
  }

  triggerChange() {
    this.onDateChange.emit(); 
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }
}

