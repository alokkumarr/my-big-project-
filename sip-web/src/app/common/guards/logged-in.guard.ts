import { Injectable } from '@angular/core';
import { CanActivateChild, CanActivate, Router } from '@angular/router';
import { UserService } from '../../common/services';
import { BrandingService } from './../../modules/admin/branding/branding.service';
import * as isEmpty from 'lodash/isEmpty';

@Injectable()
export class IsUserLoggedInGuard implements CanActivate, CanActivateChild {
  constructor(
    public _user: UserService,
    public _router: Router,
    public _brandingService: BrandingService
    ) {}

  canActivateChild() {
    return this.isUserLoggedIn();
  }

  canActivate () {
    return this.isUserLoggedIn();
  }

  isUserLoggedIn() {
    if (this._user.isLoggedIn()) {
      console.log('useer loged in');
      this._brandingService.getBrandingDetails().subscribe(data => {
        console.log(data);
        const brandingColor = isEmpty(data.brandColor) ? '#0077be' : data.brandColor;

        this._brandingService.savePrimaryColor(brandingColor);
      });
      return true;
    }

    // redirect to login page
    setTimeout(() => this._router.navigate(['login']));
    return false;
  }
}
