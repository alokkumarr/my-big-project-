import angular from 'angular';
import findIndex from 'lodash/findIndex';
import uniqueId from 'lodash/uniqueId';

const analyses = require('./analyses.json');

export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/saveReport',
  response: (method, url, data) => {
    data = angular.fromJson(data);

    if (!data.id) {
      data.id = uniqueId('1');
      analyses.push(data);
    } else {
      const index = findIndex(analyses, analyse => {
        return analyse.id === data.id;
      });

      analyses[index] = data;
    }

    return [200, data];
  }
};
