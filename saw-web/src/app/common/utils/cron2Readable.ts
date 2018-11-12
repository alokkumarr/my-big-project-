import cronstrue from 'cronstrue';
import * as isEmpty from 'lodash/isEmpty';
import * as moment from 'moment';

export function generateSchedule(cron) {
  if (!cron) {
    return '';
  }
  const jobDetails = cron.jobDetails;
  const expression = jobDetails.cronExpression;
  if (isEmpty(expression)) {
    return '';
  }
  if (jobDetails.activeTab === 'hourly') {
    // there is no time stamp in hourly cron hence converting to utc and local is not required.
    const localMinuteCron = extractMinute(expression);
    return cronstrue.toString(localMinuteCron);
  }
  const localCron = convertToLocal(expression);
  return cronstrue.toString(localCron);
}

function extractMinute(CronUTC) {
  const splitArray = CronUTC.split(' ');
  const date = new Date();
  const hour = parseInt(moment().format('HH'), 10);
  date.setUTCHours(hour, splitArray[1]);
  const UtcTime = moment.utc(date).local().format('mm').split(' ');
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
  date.setUTCHours(splitArray[2], splitArray[1]);
  const UtcTime = moment.utc(date).local().format('mm HH').split(' ');
  splitArray[1] = UtcTime[0];
  splitArray[2] = UtcTime[1];
  return splitArray.join(' ');
}
