import { Injectable } from '@angular/core';
import { CanActivateChild, CanActivate } from '@angular/router';
import { UserService } from '../../common/services';

@Injectable()
export class IsUserLoggedInGuard implements CanActivate, CanActivateChild {
  constructor(private _user: UserService) {}

  canActivateChild() {
    return this.isUserLoggedIn();
  }

  canActivate () {
    return this.isUserLoggedIn();
  }

  isUserLoggedIn() {
    if (this._user.isLoggedIn()) {
      return true;
    }

    // redirect to login page
    setTimeout(() => window.location.assign('./login.html'));
    return false;
  }
}
