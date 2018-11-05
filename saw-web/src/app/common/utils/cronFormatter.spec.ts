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
    isValid('0 0 * ? * 1/3 *');
    expect(isValid('0 0 * ? * 1/3 *')).toBeTruthy();
    isValid('0 0 * ? * 1/3 *');
  });

  it('should convert time to UTC timezone', () => {
    convertToUtc(10, 30);
    expect(convertToUtc(10, 30)).not.toBeNull();
    convertToUtc(10, 30);
  });

  it('should convert time saved inside cron expression to Local timezone', () => {
    convertToLocal('0 0 * ? * 1/3 *');
    expect(convertToLocal('0 0 * ? * 1/3 *')).not.toBeNull();
    convertToLocal('0 0 * ? * 1/3 *');
  });

  it('should generate cron for hourly basis', () => {
    generateHourlyCron(10, 30);
    expect(generateHourlyCron(10, 30)).not.toBeNull();
    generateHourlyCron(10, 30);
  });

  it('should generate cron for daily basis', () => {
    generateDailyCron(10, 30);
    expect(generateDailyCron(10, 30)).not.toBeNull();
    generateDailyCron(10, 30);
  });

  it('should generate cron for weekly basis', () => {
    generateWeeklyCron(10, 30);
    expect(generateWeeklyCron(10, 30)).not.toBeNull();
    generateWeeklyCron(10, 30);
  });

  it('should generate cron for monthly basis', () => {
    generateMonthlyCron(10, 30);
    expect(generateMonthlyCron(10, 30)).not.toBeNull();
    generateMonthlyCron(10, 30);
  });

  it('should generate cron for monthly basis', () => {
    generateYearlyCron(10, 30);
    expect(generateYearlyCron(10, 30)).not.toBeNull();
    generateYearlyCron(10, 30);
  });
});
