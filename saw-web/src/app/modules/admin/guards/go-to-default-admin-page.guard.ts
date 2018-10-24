import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable()
export class GoToDefaultAdminPageGuard implements CanActivate {
  constructor(private _router: Router) {}

  canActivate() {
    setTimeout(() => {
      this._router.navigate(['admin', 'user']);
    }, 100);
    return true;
  }
}
