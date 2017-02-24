import find from 'lodash/find';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/lastPublishedAnalysis/:id',
  response: (method, url, data, headers, keys) => {
    const id = parseInt(keys.id, 10);
    const publishedAnalyses = require('./publishedAnalysesDetail.json');
    const lastPublishedAnalysis = find(publishedAnalyses, analysis => {
      return analysis.analysisId === id;
    });

    if (lastPublishedAnalysis) {
      return [200, lastPublishedAnalysis];
    }

    return [404, {}];
  }
};
