import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable()
export class DefaultWorkbenchPageGuard implements CanActivate {
  constructor(private _router: Router) {}

  canActivate() {
    /**
    * Temporary fix for routing of SQL component until the issue of editor initialization is figured out.
    * So on refresh re-routin to listing page
    */
    setTimeout(() => this._router.navigate(['workbench', 'dataobjects']));
    return true;
  }
}
