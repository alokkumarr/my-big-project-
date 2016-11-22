export default routesConfig;

/** @ngInject */
function routesConfig($stateProvider, $urlRouterProvider, $locationProvider) {
  $locationProvider.html5Mode(true).hashPrefix('!');
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'controls',
      url: '/',
      component: 'controlsSection'
    }, {
      name: 'pivotgrid',
      url: '/pivotgrid',
      component: 'pivotgridSection'
    }, {
      name: 'sqlTable',
      url: '/sqlTable',
      component: 'sqlTableSection'
    }, {
      name: 'accordionMenu',
      url: '/accordionMenu',
      component: 'accordionMenuSection'
    }, {
      name: 'panel',
      url: '/panel',
      component: 'panelSection'
    }, {
      name: 'charts',
      url: '/charts',
      component: 'chartsSection'
    }, {
      name: 'modals',
      url: '/modals',
      component: 'modalsSection'
    }, {
      name: 'snapshotKpi',
      url: '/snapshotKpi',
      component: 'snapshotKpiSection'
    }, {
      name: 'analysisCard',
      url: '/analysisCard',
      component: 'analysisCardSection'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
