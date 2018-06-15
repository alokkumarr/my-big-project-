import { Component, Input, EventEmitter, Output, OnInit } from '@angular/core';
const template = require('./cron-date-picker.component.html');

@Component({
  selector: 'cron-date-picker',
  template
})
export class CronDatePickerComponent implements OnInit {

  @Input() public model: any;
  @Output() onDateChange = new EventEmitter();

  ngOnInit() {
    this.selectOptions = {
      hours: this.range(1,12),
      minutes: ['00','01','02','03','04','05','06','07','08','09','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30','31','32','33','34','35','36','37','38','39','40','41','42','43','44','45','46','47','48','49','50','51','52','53','54','55','56','57','58','59'],
      hourTypes: ['AM', 'PM']
    };
  }

  triggerChange() {
    this.onDateChange.emit(this.model);
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }
}
