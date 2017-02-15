import filter from 'lodash/filter';

const analyses = require('./analyses.json');

export const RequestMock = {
  method: 'GET',
  url: 'api/analyze/analyses',
  response: (method, url, data, headers, keys) => {
    const category = keys.category;
    const items = filter(analyses, item => {
      return item.category === category;
    });

    return [200, items];
  }
};
