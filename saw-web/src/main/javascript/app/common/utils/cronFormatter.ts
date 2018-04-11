import * as moment from 'moment';

export function isValid(expression) {
  const QUARTZ_REGEX = /^\s*($|#|\w+\s*=|(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?(?:,(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?)*)\s+(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)\s+(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\s+(\?|\*|(?:[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-|\/|\,|#)(?:[1-5]))?(?:L)?(?:,(?:[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-|\/|\,|#)(?:[1-5]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*))$/;
  const formattedExpression = expression.toUpperCase();
  return !!formattedExpression.match(QUARTZ_REGEX);
}

export function convertToUtc(hourValue, minutes) {
  const date = new Date();
  date.setHours(hourValue);
  date.setMinutes(minutes);
  return moment(date).utc().format('mm HH');
}

export function convertToLocal(cronUTC) {
  const splitArray = cronUTC.split(' ');
  const date = new Date();
  date.setUTCHours(splitArray[2], splitArray[1]);
  const UtcTime = moment.utc(date).local().format('mm HH').split(' ');
  splitArray[1] = UtcTime[0];
  splitArray[2] = UtcTime[1];
  return splitArray.join(' ');

}

export function hourToCron(hour, hourType, minutes) {
  const hourValue = hourType === 'AM' ? (parseInt(hour) === 12 ? 0 : parseInt(hour)) : (parseInt(hour) === 12 ? 12 : parseInt(hour) + 12));
  const minuteHourUTC = this.convertToUtc(hourValue, minutes);
  return minuteHourUTC;
}

export function generateDailyCron(cronDaily, dateSelects) {
  if (cronDaily.dailyType === 'everyDay') {
    return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} 1/${cronDaily.days} * ? *`;
  }
  return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ? * MON-FRI *`;
}

export function generateWeeklyCron(cronWeek, dateSelects) {
  return this.CronExpression = `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ? * ${cronWeek} *`;
}

export function generateMonthlyCron(cronMonth, dateSelects) {
  if (cronMonth.monthlyType === 'monthlyDay') {
    return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ${cronMonth.specificDay} 1/${cronMonth.specificMonth} ? *`;
  }
  return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ? 1/${cronMonth.specificWeekDayMonthWeek} ${cronMonth.specificWeekDayDay}${cronMonth.specificWeekDayMonth} *`;
}

export function generateYearlyCron(cronYear, dateSelects) {
  if (cronYear.yearlyType === 'yearlyMonth') {
    return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ${cronYear.specificMonthDayDay} ${cronYear.specificMonthDayMonth} ? *`;
  }
  return `0 ${this.hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ? ${cronYear.specificMonthWeekMonth} ${cronYear.specificMonthWeekDay}${cronYear.specificMonthWeekMonthWeek} *`;
}



