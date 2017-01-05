export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'app.alerts',
      url: 'alerts'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
