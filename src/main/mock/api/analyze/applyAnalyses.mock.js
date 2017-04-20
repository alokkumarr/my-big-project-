export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/apply',
  response: () => {
    return [200, require('./applyAnalyses.json')];
  }
};
