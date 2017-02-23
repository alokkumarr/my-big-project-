import filter from 'lodash/filter';
import isEmpty from 'lodash/isEmpty';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/publishedAnalyses/:id',
  response: (method, url, data, headers, keys) => {
    const id = parseInt(keys.id, 10);
    const allAnalysisInsances = require('./publishedAnalyses.json');
    const instancesOfAnalysis = filter(allAnalysisInsances, instance => {
      return instance.ANALYSIS_ID === id;
    });

    if (!isEmpty(instancesOfAnalysis)) {
      return [200, instancesOfAnalysis];
    }

    return [404, {}];
  }
};
