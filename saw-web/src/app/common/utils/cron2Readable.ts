import cronstrue from 'cronstrue';
import * as isEmpty from 'lodash/isEmpty';
import * as isUndefined from 'lodash/isUndefined';
import * as isString from 'lodash/isString';
import * as moment from 'moment-timezone';

export function generateSchedule(cronExpression, activeTab, timezone) {
  if (isUndefined(cronExpression) && isUndefined(activeTab)) {
    return '';
  } else if (activeTab === 'immediate') {
    // cronExpression won't be present if it's an immediate scheduled entity.
    return '';
  } else if (!isString(cronExpression)) {
    throw new Error(
      `generateSchedule expects a string as a first parameter, not: ${typeof cronExpression}`
    );
  }
  if (isEmpty(cronExpression)) {
    return '';
  }
  if (activeTab === 'hourly') {
    // there is no time stamp in hourly cron hence converting to utc and local is not required.
    const localMinuteCron = extractMinute(cronExpression, timezone);
    return cronstrue.toString(localMinuteCron);
  }
  const localCron = convertToLocal(cronExpression, timezone);
  return cronstrue.toString(localCron);
}

function extractMinute(CronUTC, timezone) {
  const splitArray = CronUTC.split(' ');
  const date = new Date();
  const hour = parseInt(moment().format('HH'), 10);
  date.setHours(hour, splitArray[1]);
  const UtcTime = moment(date)
    .format('mm HH');
  const timeInLocal = moment(UtcTime).tz(timezone).format('mm HH').split(' ');
  splitArray[1] = timeInLocal[0];
  if (UtcTime[0] === 'Invalid') {
    return CronUTC;
  } else {
    return splitArray.join(' ');
  }
}

function convertToLocal(CronUTC, timezone) {
  const splitArray = CronUTC.split(' ');
  const date = new Date();
  date.setHours(splitArray[2], splitArray[1]);
  const UtcTime = moment(date)
    .format('mm HH');
    const timeInLocal = moment(UtcTime).tz(timezone).format('mm HH').split(' ');
  splitArray[1] = timeInLocal[0];
  splitArray[2] = timeInLocal[1];
  return splitArray.join(' ');
}
