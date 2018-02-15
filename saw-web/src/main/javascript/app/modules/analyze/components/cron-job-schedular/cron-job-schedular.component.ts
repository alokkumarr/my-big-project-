declare const require: any;

import { Component, Inject, Input } from '@angular/core';
const template = require('./cron-job-schedular.component.html');
require('./cron-job-schedular.component.scss');

@Component({
  selector: 'cron-job-schedular',
  template
})

export class CronJobSchedularComponent {
  constructor() 
  public state: any;
  public model: any;
  @Input() public model: any;

  ngOnInit() {
  	this.model = {
  	  hour: '2',
  	  minute: '3',
  	  second: '4',
  	  hourType: 'PM'
  	}
  	console.log(this.model);
  	this.scheduleType = 'daily';
    this.days = this.range(0, 31);
    this.months = this.range(1,12);
    this.weeks = ['First', 'Second', 'Third', 'Fourth', 'Fifth', 'Last'];
    this.dayStrings = ['Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'];
    this.monthStrings = ['January','Febuary','March','April','May','June','July','August','September','October','November','December'];
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }

  openSchedule(event, scheduleType) {
  	this.scheduleType = scheduleType;
  }

  regenerateCron(event) {
    /*switch (this.scheduleType) {
      case 'daily':
        switch (this.state.daily.subTab) {
        case 'everyDay':
          this.cronExp = `${this.state.daily.everyDays.seconds} ${this.state.daily.everyDays.minutes} ${this.hourToCron(this.state.daily.everyDays.hours, this.state.daily.everyDays.hourType)} 1/${this.state.daily.everyDays.days} * ? *`;
          break;
        case 'everyWeek':
           this.cronExp = `${this.state.daily.everyWeekDay.seconds} ${this.state.daily.everyWeekDay.minutes} ${this.hourToCron(this.state.daily.everyWeekDay.hours, this.state.daily.everyWeekDay.hourType)} ? * MON-FRI *`;
          break;
        default:
          throw 'Invalid cron daily subtab selection';
        }
        break;
      default:
      	console.log('wrong selection');*/
  }

}

