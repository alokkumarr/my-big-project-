export const RequestMock = {
  method: 'POST',
  url: '/api/analyze/generateQuery',
  response: () => {
    return [200, require('./generateQuery.json')];
  }
};
