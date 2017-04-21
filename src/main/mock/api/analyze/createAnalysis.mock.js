export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/create',
  response: () => {
    return [200, require('./createAnalysis.json')];
  }
};
