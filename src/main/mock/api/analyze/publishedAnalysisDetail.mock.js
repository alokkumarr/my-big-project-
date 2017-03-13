import find from 'lodash/find';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/publishedAnalysis/:id',
  response: (method, url, data, headers, keys) => {
    const id = parseInt(keys.id, 10);
    const publishedAnalyses = require('./publishedAnalysesDetail.json');
    const publishedAnalysis = find(publishedAnalyses, analysis => {
      return analysis.id === id;
    });

    if (publishedAnalysis) {
      return [200, publishedAnalysis];
    }

    return [404, {}];
  }
};
