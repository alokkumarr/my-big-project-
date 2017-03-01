export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/categories',
  response: () => {
    const categories = require('./categories.json');

    return [200, categories];
  }
};
