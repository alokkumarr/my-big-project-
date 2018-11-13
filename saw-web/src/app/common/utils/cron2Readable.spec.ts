import { generateSchedule } from './cron2Readable';

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
    const {cronExpression, activeTab} = hourlyCron;
    expect(generateSchedule(cronExpression, activeTab)).toEqual('At 02 minutes past the hour');
  });

  it('should generate non-hourly schedule', () => {
    const {cronExpression, activeTab} = nonHourlyCron;
    expect(generateSchedule(cronExpression, activeTab)).toEqual('At 01:30 AM, only on Monday');
  });
});
