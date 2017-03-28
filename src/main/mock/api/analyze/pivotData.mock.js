export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/pivotData',
  response: () => {
    return [200, require('./pivotData.json')];
  }
};
