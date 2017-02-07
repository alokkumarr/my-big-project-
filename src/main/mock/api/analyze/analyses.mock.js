export const RequestMock = {
  method: 'GET',
  url: 'api/analyze/analyses',
  response: () => {
    return [200, require('./analyses.json')];
  }
};
