import filter from 'lodash/filter';
import find from 'lodash/find';

const analyses = require('./analyses.json');

export const RequestMock = {
  method: 'GET',
  url: 'api/analyze/analyses',
  response: (method, url, data, headers, keys) => {
    const category = keys.category;
    const query = angular.fromJson(keys.query) || {};
    let items = filter(analyses, item => {
      return item.category === category;
    });

    if (query.filter) {
      const term = query.filter.toUpperCase();
      const matchIn = item => {
        return (item || '').toUpperCase().indexOf(term) !== -1;
      };

      items = filter(items, item => {
        return matchIn(item.name) ||
          matchIn(item.type) ||
          find(item.metrics, metric => matchIn(metric));
      });
    }

    return [200, items];
  }
};
