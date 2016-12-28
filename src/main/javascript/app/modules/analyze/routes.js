export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'app.analyze',
      url: 'analyze',
      component: 'analyzePage'
    }, {
      name: 'app.analyze.view',
      url: '/:id',
      component: 'analyzeView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
