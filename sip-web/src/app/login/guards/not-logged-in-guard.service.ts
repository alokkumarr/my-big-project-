import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild, Router } from '@angular/router';
import { UserService } from '../../common/services';

@Injectable()
export class IsUserNotLoggedInGuard implements CanActivate, CanActivateChild {
  constructor(
    public _user: UserService,
    public _router: Router
    ) {}

  canActivate() {
    return this.isLoggedOut();
  }

  canActivateChild() {
    return this.isLoggedOut();
  }

  isLoggedOut() {
    if (this._user.isLoggedIn()) {
      // redirect to app
      setTimeout(() => this._router.navigate(['']));
      return false;
    }
    return true;
  }
}
