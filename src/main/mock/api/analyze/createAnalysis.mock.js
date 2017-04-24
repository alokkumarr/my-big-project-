import find from 'lodash/find';

export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/createAnalysis',
  response: (method, url, params) => {
    params = angular.fromJson(params);
    const analyses = require('./createAnalyses.json');
    const analysis = find(analyses, ({type}) => type === params.keys[0].analysisType);

    if (analysis) {
      return [200, analysis];
    }

    return [404, {}];
  }
};
