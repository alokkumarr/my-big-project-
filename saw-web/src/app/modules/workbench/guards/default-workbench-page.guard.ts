import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { JwtService } from '../../../common/services';

@Injectable()
export class DefaultWorkbenchPageGuard implements CanActivate {
  constructor(
    private _router: Router,
    private _jwt: JwtService
    ) {}

  canActivate() {
    const redirectRoute = this._jwt.isAdmin() ? 'datasource/create' : 'dataobjects';
    setTimeout(() => this._router.navigate(['workbench', redirectRoute]));
    return true;
  }
}
