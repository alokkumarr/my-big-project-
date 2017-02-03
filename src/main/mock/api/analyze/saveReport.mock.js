export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/saveReport',
  response: () => {
    return [200, require('./saveReport.json')];
  }
};
