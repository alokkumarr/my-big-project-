import day from './Day.json';
import week from './Week.json';
import month from './Month.json';
import quarter from './Quarter.json';

export default [
  buildMock('day', day),
  buildMock('week', week),
  buildMock('month', month),
  buildMock('quarter', quarter)
];

function buildMock(endpoint, data) {
  return {
    method: 'GET',
    url: `/api/transactionVolume/${endpoint}`,
    response: () => {
      return [200, [
        data
      ]];
    }
  };
}
