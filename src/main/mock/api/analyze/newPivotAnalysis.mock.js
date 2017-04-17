export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/newPivotAnalysis',
  response: () => {
    return [200, require('./newPivotAnalysis.json')];
  }
};
