export const ObserveMenu = {
  method: 'GET',
  url: '/api/menu/observe',
  response: () => {
    return [200, getMenu()];
  }
};

function getMenu() {
  return [{
    name: 'Dashboard 1'
  }, {
    name: 'Dashboard 2'
  }, {
    name: 'Dashboard 3'
  }, {
    name: 'Dashboard 4'
  }];
}
