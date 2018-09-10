import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { UserService } from '../../../login/services/user.service';

@Injectable()
export class IsUserLoggedInGuard implements CanActivate {
  constructor(private _user: UserService) {}

  canActivate() {
    if (this._user.isLoggedIn()) {
      return true;
    }

    // redirect to login page
    setTimeout(() => window.location.assign('./login.html'));
    return false;
  }
}
