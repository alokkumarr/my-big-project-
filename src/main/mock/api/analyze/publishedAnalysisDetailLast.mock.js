import find from 'lodash/find';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/lastPublishedAnalysis/:id',
  response: (method, url, data, headers, keys) => {
    const id = parseInt(keys.id, 10);
    const publishedAnalyses = require('./publishedAnalysesDetail.json');
    const lastPublishedAnalysis = find(publishedAnalyses, analysis => {
      // TODO, ask Suren, or Saurav to clear up the workflow of executing and publishing an analysis.
      // it should not be scheduled, so we don't get the analysis that was just executed,
      // as that would be the last one created, but that should mean, that someone is about to publish it
      // but we want the last one that was published
      return analysis.analysisId === id && analysis.scheduled;
    });

    if (lastPublishedAnalysis) {
      switch (lastPublishedAnalysis.type) {
        case 'report':
          lastPublishedAnalysis.report.data = require('./dataByQuery.json');
          break;
        case 'pivot':
          lastPublishedAnalysis.pivot.data = require('./pivotData.json');
          break;
        case 'chart':
        default:
          lastPublishedAnalysis.chart.data = require('./dataByQuery.json');
          break;
      }
      return [200, lastPublishedAnalysis];
    }

    return [404, {}];
  }
};
