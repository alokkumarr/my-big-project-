export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'observe',
      url: '/observe?dashboardId',
      component: 'observePage'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
