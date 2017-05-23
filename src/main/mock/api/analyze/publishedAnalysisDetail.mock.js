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
      switch (publishedAnalysis.type) {
        case 'report':
          publishedAnalysis.report.data = require('./dataByQuery.json');
          break;
        case 'pivot':
          publishedAnalysis.pivot.data = require('./pivotData.json').aggregations.filtered.row_level_1;
          break;
        case 'chart':
        default:
          publishedAnalysis.chart.data = require('./dataByQuery.json');
          break;
      }
      return [200, publishedAnalysis];
    }

    return [404, {}];
  }
};
