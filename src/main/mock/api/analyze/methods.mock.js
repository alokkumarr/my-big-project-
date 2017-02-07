export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/methods',
  response: () => {
    return [200, require('./methods.json')];
  }
};
