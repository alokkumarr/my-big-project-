import { correctTimeInterval } from './time-interval-parser';

describe('Time interval parser', () => {
  it('should return empty string for empty string', () => {
    expect(correctTimeInterval('')).toEqual('');
  });

  it('should return the same of it is correct', () => {
    const str = '3w 5d';
    expect(correctTimeInterval(str)).toEqual(str);
  });

  it('should return emty string for invalid time interval', () => {
    const str = '3w 5d dfdsf';
    expect(correctTimeInterval(str)).toEqual('3w 5d');
  });

  it('should correct the time interval', () => {
    const str = '3w 8d';
    expect(correctTimeInterval(str)).toEqual('4w 1d');
  });

  it('should correct the time interval', () => {
    const str = '1w 1d 48h';
    expect(correctTimeInterval(str)).toEqual('1w 3d');
  });

  it('should correct the time interval', () => {
    const str = '1w 5d 48h';
    expect(correctTimeInterval(str)).toEqual('2w');
  });

  it('should correct the time interval', () => {
    const str = '1w 5d 49h';
    expect(correctTimeInterval(str)).toEqual('2w 1h');
  });

  it('should correct the time interval', () => {
    const str = '1w 5d 49h 3h';
    expect(correctTimeInterval(str)).toEqual('2w 4h');
  });
});
