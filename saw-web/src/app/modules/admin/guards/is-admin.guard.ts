import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild } from '@angular/router';
import { JwtService } from '../../../common/services';

@Injectable()
export class IsAdminGuard implements CanActivate, CanActivateChild {
  constructor(private _jwt: JwtService) {}

  canActivate() {
    const isAdmin = this._jwt.isAdmin();
    return isAdmin;
  }

  canActivateChild() {
    const isAdmin = this._jwt.isAdmin();
    return isAdmin;
  }
}
