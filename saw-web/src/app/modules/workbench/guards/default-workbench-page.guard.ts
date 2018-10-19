import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable()
export class DefaultWorkbenchPageGuard implements CanActivate {
  constructor(private _router: Router) {}

  canActivate() {
    setTimeout(() => this._router.navigate(['workbench', 'dataobjects']));
    return true;
  }
}
