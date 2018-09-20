import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, Router } from '@angular/router';

@Injectable()
export class DefaultModuleGuard implements CanActivate {
  constructor(private _router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    setTimeout(() => {
      this._router.navigate(['analyze']);
    });
    return false;
  }
}
