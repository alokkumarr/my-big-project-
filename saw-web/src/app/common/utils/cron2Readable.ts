import cronstrue from 'cronstrue';
import * as isEmpty from 'lodash/isEmpty';
import * as isUndefined from 'lodash/isUndefined';
import * as isString from 'lodash/isString';
import * as moment from 'moment';

export function generateSchedule(cronExpression, activeTab) {
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
    const localMinuteCron = extractMinute(cronExpression);
    return cronstrue.toString(localMinuteCron);
  }
  const localCron = convertToLocal(cronExpression);
  return cronstrue.toString(localCron);
}

function extractMinute(CronUTC) {
  const splitArray = CronUTC.split(' ');
  const date = new Date();
  const hour = parseInt(moment().format('HH'), 10);
  date.setHours(hour, splitArray[1]);
  const UtcTime = moment(date)
    .local()
    .format('mm')
    .split(' ');
  splitArray[1] = UtcTime[0];
  if (UtcTime[0] === 'Invalid') {
    return CronUTC;
  } else {
    return splitArray.join(' ');
  }
}

function convertToLocal(CronUTC) {
  const splitArray = CronUTC.split(' ');
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
