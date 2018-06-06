import { Component, Input, EventEmitter, Output } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material';
import {FormControl} from '@angular/forms';
import * as clone from 'lodash/clone';
import * as isUndefined from 'lodash/isUndefined';
import cronstrue from 'cronstrue';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as moment from 'moment';
import '../../../../../../../../node_modules/ng-pick-datetime/assets/style/picker.min.css';

import {
  getLocalMinute, generateHourlyCron, generateDailyCron, generateWeeklyCron, generateMonthlyCron, generateYearlyCron, isValid, convertToLocal
} from '../../../../common/utils/cronFormatter';

import { SCHEDULE_TYPES } from '../../../../common/consts';

const template = require('./cron-job-schedular.component.html');
require('./cron-job-schedular.component.scss');

@Component({
  selector: 'cron-job-schedular',
  template
})

export class CronJobSchedularComponent {
  @Input() public model: any;
  @Input() public crondetails: any;
  @Output() onCronChanged: EventEmitter<any> = new EventEmitter();
  public startAt = new Date();
  NumberMapping: any = {'=1': '#st', '=2': '#nd', '=3': '#rd', 'other': '#th'};
  DayMapping: any = {'=TUE': 'TUESDAY', '=WED': 'WEDNESDAY', '=THU': 'THURSDAY', '=SAT': 'SATURDAY', 'other': '#DAY'};

  ngOnInit() {
  	this.dailyTypeDay = {
  	  hour: '',
  	  minute: '',
  	  second: '',
  	  hourType: 'AM'
  	};
    this.schedules = SCHEDULE_TYPES;
  	this.dailyTypeWeek = clone(this.dailyTypeDay);
  	this.weeklybasisDate = clone(this.dailyTypeDay);
  	this.specificDayMonth = clone(this.dailyTypeDay);
  	this.specificWeekDayMonth = clone(this.dailyTypeDay);
  	this.specificMonthDayYear = clone(this.dailyTypeDay);
  	this.specificMonthWeekYear = clone(this.dailyTypeDay);
    this.immediateTime = clone(this.dailyTypeDay);
  	this.model = {};
    this.immediate = {};
    this.hourly = {};
  	this.daily = {};
  	this.weekly = {};
  	this.monthly = {};
  	this.yearly = {};
    this.selectedMoments = [];
    this.selectedMoments.push(new Date(moment().local().format()));

    this.hours = this.range(0,23);
    this.minutes = this.range(0,59);
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
    this.scheduleType = 'immediate';
    this.immediate.immediatetype = '';
    if (!isEmpty(this.crondetails)) {
      this.loadData();
    }
    //this.startDate = new Date();
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
    this.immediateTime = clone(this.dailyTypeDay);
    this.model = {};
    this.hourly = {};
    this.immediate = {};
    this.daily = {};
    this.weekly = {};
    this.monthly = {};
    this.yearly = {};
  }

  openSchedule(scheduleType) {
    this.resetData();
    if (scheduleType.tab.textLabel.toLowerCase() === 'weekly') {
      this.scheduleType = 'weeklybasis'
    } else {
      this.scheduleType = scheduleType.tab.textLabel.toLowerCase();
    }
  }

  onDateChange(event) {
    this.regenerateCron(event);
  }

  generateImmediateSchedule(value) {
    this.scheduleType = 'immediate';
    this.immediate.immediatetype = 'currenttime';
    this.regenerateCron('');
  }

  regenerateCron(dateSelects) {
    switch (this.scheduleType) {
    case 'immediate':
      if (this.immediate.immediatetype === 'currenttime') {
        this.activeRadio = this.immediate.immediatetype;
        this.cronChange();
      }
      break;
    case 'hourly':
      //Generating Cron expression for selections made in hourly tab
      this.CronExpression = generateHourlyCron(this.hourly.hours, this.hourly.minutes);
      if (isValid(this.CronExpression)) {
        this.activeRadio = '';
        this.cronChange();
      }
      break;
    case 'daily':
      //Generating Cron expression for selections made in daily tab
      this.CronExpression = generateDailyCron(this.daily, dateSelects);
      if (isValid(this.CronExpression)) {
        this.activeRadio = this.daily.dailyType;
        this.cronChange();
      }
      break;
    case 'weeklybasis':
      //Generating Cron expression for selections made in weekly tab
      const days = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN']
          .reduce((acc, day) => this.weekly[day] ? acc.concat([day]) : acc, [])
          .join(',');
      this.CronExpression = generateWeeklyCron(days, dateSelects);
      if (isValid(this.CronExpression)) {
        this.activeRadio = '';
        this.cronChange();
      }
      break;
    case 'monthly':
      //Generating Cron expression for selections made in monthly tab
      this.CronExpression = generateMonthlyCron(this.monthly, dateSelects);
      if (isValid(this.CronExpression)) {
        this.activeRadio = this.monthly.monthlyType;
        this.cronChange();
      }
      break;
    case 'yearly':
      //Generating Cron expression for selections made in yearly tab
      this.CronExpression = generateYearlyCron(this.yearly, dateSelects);
      if (isValid(this.CronExpression)) {
        this.activeRadio = this.yearly.yearlyType;
        this.cronChange();
      }
      break;
    }
  }

  cronChange() {
    this.startDate = '';
    this.endDate = '';
    if (this.scheduleType !== 'immediate') {
      this.startDate = (isUndefined(this.selectedMoments[0]) || this.selectedMoments[1] === null ? moment.utc() : this.selectedMoments[0]);
      this.endDate = (isUndefined(this.selectedMoments[1]) || this.selectedMoments[1] === null ? '' : this.selectedMoments[1]);
    }
    this.crondetails = {
      cronexp: this.CronExpression,
      activeTab: this.scheduleType,
      activeRadio: this.activeRadio,
      startDate: this.startDate,
      endDate: this.endDate
    }
    this.onCronChanged.emit(this.crondetails);
  }

  loadData() {
    this.CronExpression = this.crondetails.cronexp;
    this.onCronChanged.emit(this.crondetails);
    this.scheduleType = this.crondetails.activeTab;
    this.activeRadio = this.crondetails.activeRadio;
    this.selectedMoments = [];
    this.selectedMoments.push(new Date(moment(this.crondetails.startDate).local()));
    if (!isUndefined(this.crondetails.endDate) && this.crondetails.endDate !== null) {
      this.selectedMoments.push(new Date(moment(this.crondetails.endDate).local()));
    }

    if (isEmpty(this.crondetails.cronexp)) {
      return;
    }
    if (this.scheduleType === 'hourly') {
      let parseCronValue = cronstrue.toString(this.crondetails.cronexp).split(' ');
    } else {
      const localCronExpression = convertToLocal(this.crondetails.cronexp);
      let parseCronValue = cronstrue.toString(localCronExpression).split(' ');
      let fetchTime = parseCronValue[1].split(':');
      let meridium = parseCronValue[2].split(',');
      let modelDate = {
        hour: parseInt(fetchTime[0]),
        minute: fetchTime[1],
        hourType: meridium[0]
      };
    }

    switch (this.scheduleType) {
    case 'hourly':
      const fetchLocalMinute;
      this.selectedTab = 1;
      if (this.crondetails.cronexp.match(/\d+ 0\/\d+ \* 1\/1 \* \? \*/)) {
        this.hourly.hours = 0;
        fetchLocalMinute = parseCronValue[1].split('/');
        this.hourly.minutes = (isNaN(parseInt(fetchLocalMinute[0])) ? 1 : parseInt(fetchLocalMinute[0]));
      } else {
        //Loading/displying values for Cron expression for Hourly tab selection in UI Templete.
        this.hourly.hours = (isNaN(parseInt(parseCronValue[7])) ? 1 : parseInt(parseCronValue[7]));
        this.hourly.minutes = (isNaN(parseInt(parseCronValue[1])) ? 0 : getLocalMinute(parseInt(parseCronValue[1])));
      }
      break;
    case 'daily':
      this.selectedTab = 2;
      //Loading/displying values for Cron expression for daily tab selection in UI Templete.
      this.daily.dailyType = this.crondetails.activeRadio;
      if (this.daily.dailyType === 'everyDay') {
        //First Radio Button: Under daily tab loading data when first radio button is selected.
        this.dailyTypeDay = clone(modelDate); //Loading time values for daily tab under first radio button
        if (isUndefined(parseCronValue[4])) {
          parseCronValue[4] = '1';
        }
        this.daily.days = parseInt(parseCronValue[4]);
      } else {
        //Second Raio Button: Under daily tab loading data when second radio button is selected.
        this.dailyTypeWeek = clone(modelDate);//Loading time values for daily tab under Second radio button
      }
      break;
    case 'weeklybasis':
      this.selectedTab = 3;
      //Loading/displying values for Cron expression for daily tab selection in UI Templete.
      let getWeekDays = this.crondetails.cronexp.split(' ');
      forEach(getWeekDays[5].split(','), day => {
        this.weekly[day] = true;
      })

      this.weeklybasisDate = clone(modelDate); //Loading time values for weekly tab
      break;
    case 'monthly':
      this.selectedTab = 4;
      //Loading/displying values for Cron expression for monthly tab selection in UI Templete.
      this.monthly.monthlyType = this.crondetails.activeRadio;
      if (this.monthly.monthlyType === 'monthlyDay') {
        //First Radio Button: Under monthly tab loading data when first radio button is selected.
        this.monthly.specificDay = parseInt(parseCronValue[5]);
        if (isUndefined(parseCronValue[10])) {
          parseCronValue[10] = '1';
        }
        this.monthly.specificMonth = parseInt(parseCronValue[10]);
        this.specificDayMonth = clone(modelDate); //Loading time values for monthly tab under first radio button
      } else {
        //Second Raio Button: Under monthly tab loading data when second radio button is selected.
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
        this.specificWeekDayMonth = clone(modelDate); //Loading time values for monthly tab under second radio button
      }
      break;
    case 'yearly':
      this.selectedTab = 5;
      //Loading/displying values for Cron expression for yearly tab selection in UI Templete.
      this.yearly.yearlyType = this.crondetails.activeRadio;
      if (this.yearly.yearlyType === 'yearlyMonth') {
        //First Radio Button: Under yearly tab loading data when first radio button is selected.
        this.specificMonthDayYear = clone(modelDate); //Loading time values for yearly tab under first radio button
        this.yearly.specificMonthDayMonth = new Date(Date.parse(parseCronValue[11] +' 1, 2018')).getMonth() + 1;
        this.yearly.specificMonthDayDay = parseInt(parseCronValue[5]);
      } else {
        //Second Raio Button: Under yearly tab loading data when second radio button is selected.
        this.specificMonthWeekYear = clone(modelDate); //Loading time values for yearly tab under second radio button
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
