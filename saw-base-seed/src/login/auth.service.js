/**
 * Created by ssom0002 on 12/8/2016.
 */
export class AuthService {
  constructor($state, $window) {
    this.authorized = false;
    this.memorizedState = null;
    this._$window = $window;
  }
  clear() {
    this.authorized = false;
    this.memorizedState = null;
  }
  go(fallback) {
    this.authorized = true;
    const targetState = this.memorizedState ? this.memorizedState : fallback;
    const baseUrl = this._$window.location.origin;
    const appUrl = `${baseUrl}` + targetState;
    this._$window.location = appUrl;
  }
}
