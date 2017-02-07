export const RequestMock = {
  method: 'GET',
  url: '/api/menu/observe',
  response: () => {
    return [200, require('./observe.json')];
  }
};
