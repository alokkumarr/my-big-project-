import { Injectable } from '@angular/core';
import { CanActivateChild, CanActivate, Router } from '@angular/router';
import { UserService } from '../../common/services';

@Injectable()
export class IsUserLoggedInGuard implements CanActivate, CanActivateChild {
  constructor(
    public _user: UserService,
    public _router: Router
    ) {}

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
    setTimeout(() => this._router.navigate(['login']));
    return false;
  }
}
