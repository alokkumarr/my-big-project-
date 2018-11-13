import { generateSchedule } from './cron2Readable';
import * as moment from 'moment';

const hourlyCron = {
  cronExpression: '0 02 0/1 1/1 * ? *',
  activeTab: 'hourly'
};

const nonHourlyCron = {
  cronExpression: '0 30 23 ? * MON *',
  activeTab: 'weeklybasis'
};

describe('Cron to Readable (cron2Readable.ts)', () => {
  it('should have generateSchedule function', () => {
    expect(generateSchedule).not.toBeNull();
  });

  it('should return empty string for empty cron expression', () => {
    expect(generateSchedule('', null)).toEqual('');
  });

  it('should generate hourly schedule', () => {
    const { cronExpression, activeTab } = hourlyCron;
    expect(generateSchedule(cronExpression, activeTab)).toEqual(
      'At 02 minutes past the hour'
    );
  });

  it('should generate non-hourly schedule for local time', () => {
    // the time format used by cronstrue library
    const timeFormat = 'hh:mm A';
    const { cronExpression, activeTab } = nonHourlyCron;
    const [, minutes, hours] = cronExpression.split(' ');
    const momentInput = {
      hours: parseInt(hours, 10),
      minutes: parseInt(minutes, 10)
    };
    const localTime = moment
      .utc(momentInput)
      .local()
      .format(timeFormat);
    expect(generateSchedule(cronExpression, activeTab)).toEqual(
      `At ${localTime}, only on Monday`
    );
  });
});
