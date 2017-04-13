export const RequestMock = {
  method: 'GET',
  url: '/api/analyze/semanticLayerData',
  response: () => {
    return [200, require('./semanticLayerData.json')];
  }
};
