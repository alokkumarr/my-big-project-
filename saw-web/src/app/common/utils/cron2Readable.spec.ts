import { generateSchedule } from './cron2Readable';
import * as moment from 'moment';

const hourlyCron = {
  cronExpression: '0 02 0/1 1/1 * ? *',
  activeTab: 'hourly',
  timezone: 'Asia/Calcutta'
};

const nonHourlyCron = {
  cronExpression: '0 30 23 ? * MON *',
  activeTab: 'weeklybasis',
  timezone: 'Asia/Calcutta'
};

const timezone = 'Asia/Calcutta';

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
      .local()
      .format(timeFormat);
    expect(generateSchedule(cronExpression, activeTab, timezone)).toEqual(
      `At ${localTime} minutes past the hour`
    );
  });

  it('should generate non-hourly schedule for local time', () => {
    // the time format used by cronstrue library
    const timeFormat = 'hh:mm A';
    const { cronExpression, activeTab, timezone } = nonHourlyCron;
    const [, minutes, hours] = cronExpression.split(' ');
    const momentInput = {
      hours: parseInt(hours, 10),
      minutes: parseInt(minutes, 10)
    };
    const localTime = moment(momentInput)
      .local()
      .format(timeFormat);
    expect(generateSchedule(cronExpression, activeTab, timezone)).toEqual(
      `At ${localTime}, only on Monday`
    );
  });
});
