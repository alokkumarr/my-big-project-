export const RequestMock = {
  method: 'POST',
  url: '/api/menu',
  response: () => [200, require('./menu.json')]
};
