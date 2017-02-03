export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/metrics',
  response: () => {
    return [200, require('./metrics.json')];
  }
};
