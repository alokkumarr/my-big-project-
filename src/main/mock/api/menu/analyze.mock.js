export const RequestMock = {
  method: 'GET',
  url: '/api/menu/analyze',
  response: () => {
    return [200, require('./analyze.json')];
  }
};
