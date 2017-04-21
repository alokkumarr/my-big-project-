import find from 'lodash/find';

export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/create',
  response: (method, url, params) => {
    params = angular.fromJson(params);
    const analyses = require('./createAnalyses.json');
    const analysis = find(analyses, ({type}) => type === params.keys[0].analysisType);

    const result = {
      data: [],
      links: {},
      contents: {
        analyze: []
      }
    };

    if (analysis) {
      result.contents.analyze.push(analysis);
      return [200, result];
    }

    return [404, result];
  }
};
