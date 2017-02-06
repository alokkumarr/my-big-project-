export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/dataByQuery',
  response: () => {
    return [200, require('./dataByQuery.json')];
  }
};
