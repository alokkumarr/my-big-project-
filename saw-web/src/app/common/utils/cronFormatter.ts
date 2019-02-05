import * as moment from 'moment';

let cronExpression = '';

export function isValid(expression) {
  // tslint:disable-next-line
  const QUARTZ_REGEX = /^\s*($|#|\w+\s*=|(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)\s+(\?|\*|(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?(?:,(?:[01]?\d|2[0-3])(?:(?:-|\/|\,)(?:[01]?\d|2[0-3]))?)*)\s+(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)\s+(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)\s+(\?|\*|(?:[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-|\/|\,|#)(?:[1-5]))?(?:L)?(?:,(?:[1-7]|MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-|\/|\,|#)(?:[1-5]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*))$/;
  const formattedExpression = expression.toUpperCase();
  return !!formattedExpression.match(QUARTZ_REGEX);
}

export function convertToUtc(hourValue, minutes) {
  const date = new Date();
  date.setHours(hourValue);
  date.setMinutes(minutes);
  return moment(date)
    .local()
    .format('mm HH');
}

export function convertToLocal(cronUTC) {
  const splitArray = cronUTC.split(' ');
  const date = new Date();
  date.setHours(splitArray[2], splitArray[1]);
  const UtcTime = moment(date)
    .local()
    .format('mm HH')
    .split(' ');
  splitArray[1] = UtcTime[0];
  splitArray[2] = UtcTime[1];
  return splitArray.join(' ');
}

export function getLocalMinute(minute) {
  const date = new Date();
  const hour = parseInt(moment().format('HH'), 10);
  date.setHours(hour, minute);
  const UtcTime = moment(date)
    .local()
    .format('mm HH')
    .split(' ');
  return parseInt(UtcTime[0], 10);
}

export function hourToCron(hour, hourType, minutes) {
  const intHour = parseInt(hour, 10);
  const hourValue =
    hourType === 'AM'
      ? intHour === 12 ? 0 : intHour
      : intHour === 12 ? 12 : intHour + 12;
  const minuteHourUTC = convertToUtc(hourValue, minutes);
  return minuteHourUTC;
}

export function generateHourlyCron(hours, minutes) {
  //const fetchUTCMinute = convertToUtc(moment().format('HH'), minutes);
  //const UTCMinute = fetchUTCMinute.split(' ');
  if (parseInt(hours, 10) === 0) {
    cronExpression = `0 0/${minutes} * 1/1 * ? *`;
  } else {
    cronExpression = `0 ${minutes} 0/${hours} 1/1 * ? *`;
  }
  return isValid(cronExpression) ? cronExpression : '';
}

export function generateDailyCron(cronDaily, dateSelects) {
  if (cronDaily.dailyType === 'everyDay') {
    cronExpression = `0 ${hourToCron(
      dateSelects.hour,
      dateSelects.hourType,
      dateSelects.minute
    )} 1/${cronDaily.days} * ? *`;
  } else {
    cronExpression = `0 ${hourToCron(
      dateSelects.hour,
      dateSelects.hourType,
      dateSelects.minute
    )} ? * MON-FRI *`;
  }
  return isValid(cronExpression) ? cronExpression : '';
}

export function generateWeeklyCron(cronWeek, dateSelects) {
  cronExpression = `0 ${hourToCron(
    dateSelects.hour,
    dateSelects.hourType,
    dateSelects.minute
  )} ? * ${cronWeek} *`;
  return isValid(cronExpression) ? cronExpression : '';
}

export function generateMonthlyCron(cronMonth, dateSelects) {
  cronExpression = cronMonth.monthlyType === 'monthlyDay' ? `0 ${
    hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ${
    cronMonth.specificDay} 1/${cronMonth.specificMonth} ? *` : `0 ${
      hourToCron(dateSelects.hour, dateSelects.hourType, dateSelects.minute)} ? 1/${
      cronMonth.specificWeekDayMonthWeek} ${cronMonth.specificWeekDayDay}${cronMonth.specificWeekDayMonth} *`;
  return isValid(cronExpression) ? cronExpression : '';
}

export function generateYearlyCron(cronYear, dateSelects) {
  const hourCron = hourToCron(
    dateSelects.hour,
    dateSelects.hourType,
    dateSelects.minute
  );
  if (cronYear.yearlyType === 'yearlyMonth') {
    cronExpression = `0 ${hourCron} ${cronYear.specificMonthDayDay} ${
      cronYear.specificMonthDayMonth
    } ? *`;
  } else {
    cronExpression = `0 ${hourCron} ? ${cronYear.specificMonthWeekMonth} ${
      cronYear.specificMonthWeekDay
    }${cronYear.specificMonthWeekMonthWeek} *`;
  }
  return isValid(cronExpression) ? cronExpression : '';
}
