import * as get from 'lodash/get';
import * as toLower from 'lodash/toLower';
import * as map from 'lodash/map';
import * as some from 'lodash/some';
import * as startsWith from 'lodash/startsWith';

export function runConfig($q, $rootScope, $state, $location, $window, JwtService, Idle, UserService, $transitions, $log) {
  'ngInject';

  $rootScope.getPageTitle = () => {
    const data = $state.$current.data;

    if (data && data.title) {
      return data.title;
    }
    return 'Synchronoss';
  };

  $transitions.onStart({}, trans => {

    const transitionPromise = $q.defer();
    const loginToken = $location.search().jwt;
    if (loginToken) {
      UserService.exchangeLoginToken(loginToken).then(data => {
        if (data) {
          // SSO token has been exchanged successfully. Redirect to main app.
          $window.location.assign('./');
          transitionPromise.resolve(false);
        } else {
          transitionPromise.resolve(true);
        }
      }, error => {
        $log.error(error);
        transitionPromise.resolve(true);
      });
    } else {
      // In case of no sso token present

      transitionPromise.resolve(true);
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
      const allowed = some(modules, moduleName => startsWith(toState, moduleName) || startsWith(toState, 'observe'));

      if (!allowed) {
        $log.error(new Error(`Privilege to access ${toState} not present.`));
      }

      return allowed;
    }
    return transitionPromise.promise;
  });

  Idle.watch();
  $rootScope.$on('IdleTimeout', event => {
    event.preventDefault();
    UserService.logout('logout');
  });
}
