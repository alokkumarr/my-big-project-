import { isValid,
         convertToUtc,
         convertToLocal,
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
    expect(hourToCron).not.toBeNull();
  });

  it('should check if a given expression is valid cron expression', () => {
    expect(isValid('0 0 * ? * 1/3 *')).toBeTruthy();
  });

  it('should generate cron for hourly basis', () => {
    expect(isValid(generateHourlyCron(17, 2))).toBeTruthy();
  });

  it('should generate cron for daily basis', () => {
    expect(isValid(generateDailyCron(dailyOption, cronTime))).toBeTruthy();
  });

  it('should generate cron for weekly basis', () => {
    expect(isValid(generateWeeklyCron('MON,TUE', cronTime))).toBeTruthy();
  });

  it('should generate cron for monthly basis', () => {
    expect(isValid(generateMonthlyCron(monthlyOptions, cronTime))).toBeTruthy();
  });

  it('should generate cron for monthly basis', () => {
    expect(isValid(generateYearlyCron(yearlyOptions, cronTime))).toBeTruthy();
  });
});
