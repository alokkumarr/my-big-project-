import find from 'lodash/find';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/category/:id',
  response: (method, url, data, headers, keys) => {
    const categories = require('./categories.json');
    console.log(categories, keys.id);
    const category = find(categories, category => {
      return category.id === keys.id;
    });

    if (category) {
      return [200, category];
    }

    return [404, {}];
  }
};
