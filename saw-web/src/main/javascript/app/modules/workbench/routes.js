
export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'workbench',
      url: '/workbench',
      component: 'workbenchPage'
    }, {
      name: 'workbench.datasets',
      url: '/datasets',
      component: 'datasetsPage'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
