import day from './Day.json';
import week from './Week.json';
import month from './Month.json';
import quarter from './Quarter.json';

export const DailyData = buildMock('day', day);
export const WeeklyData = buildMock('week', week);
export const MonthlyData = buildMock('month', month);
export const QuarterlyData = buildMock('quarter', quarter);

function buildMock(endpoint, data) {
  return {
    method: 'GET',
    url: `/api/transactionVolume/${endpoint}`,
    response: () => [200, [data]]
  };
}
