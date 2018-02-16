declare const require: any;

import { Component, Inject, Input, EventEmitter } from '@angular/core';
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
  	  hour: '',
  	  minute: '',
  	  second: '',
  	  hourType: ''
  	}
  	this.daily = {};
  	this.weekly = {};
  	this.monthly = {};
  	this.yearly = {};
  	this.scheduleType = 'daily';
    this.days = this.range(0, 31);
    this.months = this.range(1,12);
    this.weeks = [{
      value:'#1',
      label:'First'
    },{
      value:'#2',
      label:'Second'
    },{
      value:'#3',
      label:'Third'
    },{
      value:'#4',
      label:'Fourth'
    },{
      value:'#5',
      label:'Fifth'
    },{
      value:'L',
      label:'LAST'
    }];
    this.dayStrings = ['MON','TUE','WED','THU','FRI','SAT','SUN'];
    this.monthStrings = [{
      value:'1',
      label:'January'
    },{
      value:'2',
      label:'Febuary'
    },{
      value:'3',
      label:'March'
    },{
      value:'4',
      label:'April'
    },{
      value:'5',
      label:'May'
    },{
      value:'6',
      label:'June'
    },{
      value:'7',
      label:'July'
    },{
      value:'8',
      label:'August'
    },{
      value:'9',
      label:'September'
    },{
      value:'10',
      label:'October'
    },{
      value:'11',
      label:'November'
    },{
      value:'12',
      label:'December'
    }];
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }

  openSchedule(event, scheduleType) {
  	this.scheduleType = scheduleType;
  }

  onDateChange() {    
    this.regenerateCron();
  }

  hourToCron(hour, hourType) {
    return hourType === 'AM' ? (hour === 12 ? 0 : hour) : (hour === 12 ? 12 : hour + 12);
  }

  regenerateCron() {
    console.log(this.yearly.yearlyType);
    switch (this.scheduleType) {
    case 'daily':
      switch (this.daily.dailyType) {
      case 'everyDay':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} 1/${this.daily.days} * ? *`;
        break;
      case 'everyWeek':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ? * MON-FRI *`;
        break;
      default:
        throw 'Invalid cron daily subtab selection';
      }
      break;
    case 'weeklybasis':
      const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
          .reduce((acc, day) => this.weekly[day] ? acc.concat([day]) : acc, [])
          .join(',');
      this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ? * ${days} *`;
      console.log(days);
      break;
    case 'monthly':
      switch (this.monthly.monthlyType) {
      case 'specificDay':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ${this.monthly.specificDay} 1/${this.monthly.specificMonth} ? *`;
        break;
      case 'specificWeekDay':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ? 1/${this.monthly.specificWeekDayMonthWeek} ${this.monthly.specificWeekDayDay}${this.monthly.specificWeekDayMonth} *`;
        break;
      default:
        throw 'Invalid cron monthly subtab selection';
      }
      break;
    case 'yearly':
      switch (this.yearly.yearlyType) {
      case 'specificMonthDay':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ${this.yearly.specificMonthDayDay} ${this.yearly.specificMonthDayMonth} ? *`;
        break;
      case 'specificMonthWeek':
        this.cronExp = `${this.model.second} ${this.model.minute} ${this.hourToCron(this.model.hour, this.model.hourType)} ? ${this.yearly.specificMonthWeekMonth} ${this.yearly.specificMonthWeekDay}${this.yearly.specificMonthWeekMonthWeek} *`;
        break;
      default:
        throw 'Invalid cron yearly subtab selection';
      }
      break;
    default:
      console.log('wrong selection');
    }
    console.log(this.cronExp);
  }
}

