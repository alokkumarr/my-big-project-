declare const require: any;

import { Component, Inject, Input, EventEmitter, Output } from '@angular/core';
import * as clone from 'lodash/clone';
import * as isUndefined from 'lodash/isUndefined';
import cronstrue from 'cronstrue';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';

import {
  generateDailyCron, generateWeeklyCron, generateMonthlyCron, generateYearlyCron, isValid
} from '../../../../common/utils/cronFormatter';

const template = require('./cron-job-schedular.component.html');
require('./cron-job-schedular.component.scss');

@Component({
  selector: 'cron-job-schedular',
  template
})

export class CronJobSchedularComponent {
  constructor() 
  @Input() public model: any;
  @Input() public crondetails: any;
  @Output() onCronChanged: EventEmitter<any> = new EventEmitter();
  NumberMapping: any = {'=1': '#st', '=2': '#nd', '=3': '#rd', 'other': '#th'};
  DayMapping: any = {'=TUE': 'TUESDAY', '=WED': 'WEDNESDAY', '=THU': 'THURSDAY', '=SAT': 'SATURDAY', 'other': '#DAY'};

  ngOnInit() {
  	this.dailyTypeDay = {
  	  hour: '',
  	  minute: '',
  	  second: '',
  	  hourType: 'AM'
  	};
  	this.dailyTypeWeek = clone(this.dailyTypeDay);
  	this.weeklybasisDate = clone(this.dailyTypeDay);
  	this.specificDayMonth = clone(this.dailyTypeDay);
  	this.specificWeekDayMonth = clone(this.dailyTypeDay);
  	this.specificMonthDayYear = clone(this.dailyTypeDay);
  	this.specificMonthWeekYear = clone(this.dailyTypeDay);
  	this.model = {}
  	this.daily = {};
  	this.weekly = {};
  	this.monthly = {};
  	this.yearly = {};
    this.days = this.range(1, 31);
    this.months = this.range(1,12);
    this.weeks = [{
      value:'#1',
      label:'first'
    },{
      value:'#2',
      label:'second'
    },{
      value:'#3',
      label:'third'
    },{
      value:'#4',
      label:'fourth'
    },{
      value:'#5',
      label:'fifth'
    },{
      value:'L',
      label:'last'
    }];
    this.dayStrings = ['MON','TUE','WED','THU','FRI','SAT','SUN'];
    this.monthStrings = [{
      value: 1,
      label:'January'
    },{
      value: 2,
      label:'Febuary'
    },{
      value: 3,
      label:'March'
    },{
      value: 4,
      label:'April'
    },{
      value: 5,
      label:'May'
    },{
      value: 6,
      label:'June'
    },{
      value: 7,
      label:'July'
    },{
      value: 8,
      label:'August'
    },{
      value: 9,
      label:'September'
    },{
      value: 10,
      label:'October'
    },{
      value: 11,
      label:'November'
    },{
      value: 12,
      label:'December'
    }];
    this.scheduleType = 'daily';
    if (!isEmpty(this.crondetails)) {
      this.loadData();
    }
  }

  private range(start: number, end: number): number[] {
    const length = end - start + 1;
    return Array.apply(undefined, Array(length)).map((_, i) => i + start);
  }

  resetData() {
    this.dailyTypeDay = {
      hour: '',
      minute: '',
      second: '',
      hourType: 'AM'
    };
    this.dailyTypeWeek = clone(this.dailyTypeDay);
    this.weeklybasisDate = clone(this.dailyTypeDay);
    this.specificDayMonth = clone(this.dailyTypeDay);
    this.specificWeekDayMonth = clone(this.dailyTypeDay);
    this.specificMonthDayYear = clone(this.dailyTypeDay);
    this.specificMonthWeekYear = clone(this.dailyTypeDay);
    this.model = {}
    this.daily = {};
    this.weekly = {};
    this.monthly = {};
    this.yearly = {};
  }

  openSchedule(event, scheduleType) {
    this.resetData();
  	this.scheduleType = scheduleType;
  }

  onDateChange(event) {
    this.regenerateCron(event);
  }

  regenerateCron(dateSelects) {
    switch (this.scheduleType) {
    case 'daily':
      this.CronExpression = generateDailyCron(this.daily, dateSelects);
      if (isValid(this.CronExpression)) {
        this.cronChange(this.CronExpression, this.scheduleType, this.daily.dailyType);
      }
      break;
    case 'weeklybasis':
      const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
          .reduce((acc, day) => this.weekly[day] ? acc.concat([day]) : acc, [])
          .join(',');
      this.CronExpression = generateWeeklyCron(days, dateSelects);
      if (isValid(this.CronExpression)) {
        this.cronChange(this.CronExpression, this.scheduleType, '');
      }
      break;
    case 'monthly':
      this.CronExpression = generateMonthlyCron(this.monthly, dateSelects);
      if (isValid(this.CronExpression)) {
        this.cronChange(this.CronExpression, this.scheduleType, this.monthly.monthlyType);
      }
      break;
    case 'yearly':
      this.CronExpression = generateYearlyCron(this.yearly, dateSelects);
      if (isValid(this.CronExpression)) {
        this.cronChange(this.CronExpression, this.scheduleType, this.yearly.yearlyType);
      }
      break;
    }
  }

  cronChange(CronExpression, activeTab, activeRadio) {
    this.crondetails = {
      cronexp: CronExpression,
      activeTab: activeTab,
      activeRadio: activeRadio
    }
    this.onCronChanged.emit(this.crondetails);
  }

  loadData() {
    this.onCronChanged.emit(this.crondetails);
    this.scheduleType = this.crondetails.activeTab;
    let parseCronValue = cronstrue.toString(this.crondetails.cronexp).split(' ');
    let fetchTime = parseCronValue[1].split(':');
    let meridium = parseCronValue[2].split(',');
    const modelDate = {
      hour: parseInt(fetchTime[0]),
      minute: fetchTime[1],
      hourType: meridium[0]
    };

    switch (this.scheduleType) {
    case 'daily':
      this.daily.dailyType = this.crondetails.activeRadio;
      if (this.daily.dailyType === 'everyDay') {
        this.dailyTypeDay = clone(modelDate);
        this.daily.days = parseInt(parseCronValue[4]);
      } else {
        this.dailyTypeWeek = clone(modelDate);
      }
      break;
    case 'weeklybasis':
      let getWeekDays = this.crondetails.cronexp.split(' ');
      forEach(getWeekDays[5].split(','), day => {
        this.weekly[day] = true;
      })
      
      this.weeklybasisDate = clone(modelDate);
      break;
    case 'monthly':
      this.monthly.monthlyType = this.crondetails.activeRadio;
      if (this.monthly.monthlyType === 'monthlyDay') {
        this.monthly.specificDay = parseInt(parseCronValue[5]);
        this.monthly.specificMonth = parseInt(parseCronValue[10]);
        this.specificDayMonth = clone(modelDate);
      } else {
        forEach(this.weeks, week => {
          if (week.label === parseCronValue[5]) {
            this.monthly.specificWeekDayMonth = week.value;
          }
        });
        this.monthly.specificWeekDayDay = parseCronValue[6].substr(0, 3).toUpperCase();
        this.monthly.specificWeekDayMonthWeek = parseInt(parseCronValue[11]);
        if (isNaN(parseInt(parseCronValue[11]))) {
          this.monthly.specificWeekDayMonthWeek = 1;
        }
        this.specificWeekDayMonth = clone(modelDate);
      }
      break;
    case 'yearly':
      this.yearly.yearlyType = this.crondetails.activeRadio;
      if (this.yearly.yearlyType === 'yearlyMonth') {
        this.specificMonthDayYear = clone(modelDate);
        this.yearly.specificMonthDayMonth = new Date(Date.parse(parseCronValue[11] +' 1, 2018')).getMonth() + 1;
        this.yearly.specificMonthDayDay = parseInt(parseCronValue[5]);
      } else {
        this.specificMonthWeekYear = clone(modelDate);
        forEach(this.weeks, week => {
          if (week.label === parseCronValue[5]) {
            this.yearly.specificMonthWeekMonthWeek = week.value;
          }
        });
        this.yearly.specificMonthWeekDay = parseCronValue[6].substr(0, 3).toUpperCase();
        this.yearly.specificMonthWeekMonth = new Date(Date.parse(parseCronValue[12] +' 1, 2018')).getMonth() + 1;
      }
      break;
    }
  }
}
