import { generateSchedule } from './cron2Readable';
import * as moment from 'moment-timezone';
import cronstrue from 'cronstrue';

const hourlyCron = {
  cronExpression: '0 02 0/1 1/1 * ? *',
  activeTab: 'hourly',
  timezone: 'Etc/GMT'
};

const nonHourlyCron = {
  cronExpression: '0 30 03 ? * MON *',
  activeTab: 'weeklybasis',
  timezone: 'Etc/GMT'
};

const timezone = 'Etc/GMT';

describe('Cron to Readable (cron2Readable.ts)', () => {
  it('should have generateSchedule function', () => {
    expect(generateSchedule).not.toBeNull();
  });

  it('should return empty string for empty cron expression', () => {
    expect(generateSchedule('', null, timezone)).toEqual('');
  });

  it('should generate hourly schedule', () => {
    const timeFormat = 'mm';
    const { cronExpression, activeTab } = hourlyCron;
    const [, minutes] = cronExpression.split(' ');
    const momentInput = {
      hours: 1,
      minutes: parseInt(minutes, 10)
    };
    const localTime = moment(momentInput)
      .format(timeFormat);
    expect(generateSchedule(cronExpression, activeTab, timezone)).toEqual(
      `At ${localTime} minutes past the hour`
    );
  });

  it('should generate non-hourly schedule for local time', () => {
    // the time format used by cronstrue library
    const { cronExpression, activeTab, timezone } = nonHourlyCron;
    const splitArray = cronExpression.split(' ');
    const timeInLocal = moment.tz(`${splitArray[1]} ${splitArray[2]}`, 'mm HH', timezone || 'Etc/GMT').toDate();
    const extractMinuteHour = moment(timeInLocal).format('mm HH').split(' ');
    splitArray[1] = extractMinuteHour[0];
    splitArray[2] = extractMinuteHour[1];
    expect(generateSchedule(cronExpression, activeTab, timezone)).toEqual(
      cronstrue.toString(splitArray.join(' '))
    );
  });
});
