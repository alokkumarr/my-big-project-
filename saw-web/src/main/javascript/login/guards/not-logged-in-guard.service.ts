import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';
import { UserService } from '../../app/common/services';

@Injectable()
export class IsUserNotLoggedInGuard implements CanActivate {
  constructor(private _user: UserService) {}

  canActivate() {
    if (this._user.isLoggedIn()) {
      // redirect to app
      setTimeout(() => window.location.assign('./'));
      return false;
    }
    return true;
  }
}
