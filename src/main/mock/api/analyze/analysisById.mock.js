import find from 'lodash/find';

export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/byId/:id',
  response: (method, url, data, headers, keys) => {
    const analyses = require('./analyses.json');
    const analyse = find(analyses, analyse => {
      return analyse.id === keys.id;
    });

    if (analyse) {
      return [200, analyse];
    }

    return [404, {}];
  }
};

export const DeleteMock = {
  method: 'DELETE',
  url: '/api/analyze/byId/:id',
  response: (method, url, data, headers, keys) => {
    const analyses = require('./analyses.json');
    const analyse = find(analyses, analyse => {
      return analyse.id === keys.id;
    });

    if (analyse) {
      return [200, analyse];
    }

    return [404, {}];
  }
};
