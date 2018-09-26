import cronstrue from 'cronstrue';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as moment from 'moment';

export function generateSchedule(cronJobs, id) {
  let scheduleHuman = '';
  forEach(cronJobs, cron => {
    if (cron.jobDetails.analysisID === id && !isEmpty(cron.jobDetails.cronExpression)) {
      if (cron.jobDetails.activeTab === 'hourly') {
        // there is no time stamp in hourly cron hence converting to utc and local is not required.
        const localMinuteCron = extractMinute(cron.jobDetails.cronExpression);
        scheduleHuman = cronstrue.toString(localMinuteCron);
      } else {
        const localCron = convertToLocal(cron.jobDetails.cronExpression);
        scheduleHuman = cronstrue.toString(localCron);
      }
    }
  });
  return scheduleHuman;
}

function extractMinute(CronUTC) {
  const splitArray = CronUTC.split(' ');
  const date = new Date();
  date.setUTCHours(moment().format('HH'), splitArray[1]);
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
