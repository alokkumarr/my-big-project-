import { Injectable } from '@angular/core';
import { CanActivate, CanActivateChild } from '@angular/router';
import { JwtService } from '../../../../login/services/jwt.service';

@Injectable()
export class isAdminGuard implements CanActivate, CanActivateChild {
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
