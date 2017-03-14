export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/artifacts',
  response: () => {
    return [200, require('./artifacts.json')];
  }
};
