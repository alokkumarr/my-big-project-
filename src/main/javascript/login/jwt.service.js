/**
 * Created by ssom0002 on 12/6/2016.
 */
import AppConstants from './app.constants';
export function JwtService($window) {
  'ngInject';

  return {
    save,
    get,
    destroy
  };

  function save(token) {
    $window.localStorage[AppConstants.jwtKey] = token;
  }
  function get() {
    return $window.localStorage[AppConstants.jwtKey];
  }
  function destroy() {
    $window.localStorage.removeItem(AppConstants.jwtKey);
  }
}
