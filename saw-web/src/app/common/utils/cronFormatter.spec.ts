import { isValid,
         convertToUtc,
         convertToLocal,
         getLocalMinute,
         hourToCron,
         generateHourlyCron,
         generateDailyCron,
         generateWeeklyCron,
         generateMonthlyCron,
         generateYearlyCron } from './cronFormatter';

const cronTime = {hour: 2, minute: '02', second: '', hourType: 'AM'};

const dailyOption = {dailyType: 'everyDay', days: 3};
const monthlyOptions = {monthlyType: 'monthlyDay', specificDay: 3, specificMonth: 3};
const yearlyOptions = {yearlyType: 'yearlyMonth', specificMonthDayMonth: 3, specificMonthDayDay: 3};

describe('Cron Formatter', () => {
  it('should exist', () => {
    expect(isValid).not.toBeNull();
  });

  it('should exist', () => {
    expect(convertToUtc).not.toBeNull();
  });

  it('should exist', () => {
    expect(convertToLocal).not.toBeNull();
  });

  it('should exist', () => {
    expect(getLocalMinute).not.toBeNull();
  });

  it('should exist', () => {
    expect(hourToCron).not.toBeNull();
  });

  it('should check if a given expression is valid cron expression', () => {
    expect(isValid('0 0 * ? * 1/3 *')).toBeTruthy();
  });

  it('should convert time to UTC timezone', () => {
    expect(convertToUtc(17, 2)).toEqual('32 11');
  });

  it('should generate cron for hourly basis', () => {
    expect(generateHourlyCron(17, 2)).toEqual('0 32 0/17 1/1 * ? *');
  });

  it('should generate cron for daily basis', () => {
    expect(generateDailyCron(dailyOption, cronTime)).toEqual('0 32 20 1/3 * ? *');
  });

  it('should generate cron for weekly basis', () => {
    expect(generateWeeklyCron('MON,TUE', cronTime)).toEqual('0 32 20 ? * MON,TUE *');
  });

  it('should generate cron for monthly basis', () => {
    expect(generateMonthlyCron(monthlyOptions, cronTime)).toEqual('0 32 20 3 1/3 ? *');
  });

  it('should generate cron for monthly basis', () => {
    expect(generateYearlyCron(yearlyOptions, cronTime)).toEqual('0 32 20 3 3 ? *');
  });
});
