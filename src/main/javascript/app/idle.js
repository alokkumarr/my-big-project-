export function idleConfig($stateProvider, $urlRouterProvider, IdleProvider, KeepaliveProvider) {
  'ngInject';

  // configure Idle settings
  IdleProvider.idle(1200); // in seconds
  IdleProvider.timeout(1200); // in seconds
  KeepaliveProvider.interval(2); // in seconds

}
