export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'observe',
      url: '/observe',
      component: 'observePage'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
