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
    return cronstrue.toString(cronExpression);
  }
  const localCron = convertToLocal(cronExpression, timezone);
  return cronstrue.toString(localCron);
}

export function convertToLocal(cronUTC, timezone) {
  const splitArray = cronUTC.split(' ');
  const timeInLocal = moment.tz(`${splitArray[1]} ${splitArray[2]}`, 'mm HH', timezone || 'Etc/GMT').toDate();
  const extractMinuteHour = moment(timeInLocal).format('mm HH').split(' ');
  splitArray[1] = extractMinuteHour[0];
  splitArray[2] = extractMinuteHour[1];
  return splitArray.join(' ');
}
