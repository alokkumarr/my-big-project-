import { Injectable } from '@angular/core';
import { CanActivateChild } from '@angular/router';
import { UserService } from '../../../login/services/user.service';

@Injectable()
export class IsUserLoggedInGuard implements CanActivateChild {
  constructor(private _user: UserService) {}

  canActivateChild() {
    if (this._user.isLoggedIn()) {
      return true;
    }

    // redirect to login page
    setTimeout(() => window.location.assign('./login.html'));
    return false;
  }
}
