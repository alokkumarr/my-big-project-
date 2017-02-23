import filter from 'lodash/filter';

export const RequestMock = {
  method: 'GET',
  url: '/api/menu/:moduleName',
  response: (method, url, data, headers, keys) => {
    const results = filter(require('./menu.json'), item => {
      return item.module === keys.moduleName;
    });

    return [200, results];
  }
};
