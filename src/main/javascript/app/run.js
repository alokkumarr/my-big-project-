import get from 'lodash/get';
import toLower from 'lodash/toLower';
import map from 'lodash/map';
import some from 'lodash/some';
import startsWith from 'lodash/startsWith';

export function runConfig($rootScope, $state, $location, $window, JwtService, Idle, UserService, $transitions, $log) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }

    return 'Synchronoss';
  };

  $transitions.onStart({}, trans => {
    const toState = trans.to().name;
    const token = JwtService.getTokenObj();

    /* If no token present, send to login */
    if (!startsWith(toState, 'login') && !token) {
      $window.location.assign('./login.html');
      return false; // this technically doesn't matter, since we're changing the location itself, still...
    }

    /* Allow admin users to visit admin pages */
    if (JwtService.isAdmin(token) && (startsWith(toState, 'admin') || startsWith(toState, 'role') || startsWith(toState, 'privilege') || startsWith(toState, 'categories'))) {
      return true;
    }

    const modules = map(
      get(token, 'ticket.products.[0].productModules'),
      module => toLower(module.productModName)
    );

    /* See if the state we're going to is in the list of supported modules */
    const allowed = some(modules, moduleName => startsWith(toState, moduleName));

    if (!allowed) {
      $log.error(new Error(`Privilege to access ${toState} not present.`));
    }

    return allowed;
  });

  Idle.watch();
  $rootScope.$on('IdleTimeout', event => {
    event.preventDefault();
    UserService.logout('logout');
  });
}
