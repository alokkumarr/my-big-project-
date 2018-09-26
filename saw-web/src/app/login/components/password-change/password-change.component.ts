import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

const template = require('./password-change.component.html');
require ('./password-change.component.scss');

@Component({
  selector: 'password-change',
  template
})

export class PasswordChangeComponent {

  constructor(
    private _JwtService: JwtService,
    private _UserService: UserService,
    private _router: Router
    ) {}

  private formData = {
    oldPwd: null,
    newPwd: null,
    confNewPwd: null
  };

  private errorMsg;

  private formState: boolean;

  changePwd() {
    const token = this._JwtService.get();

    if (!token) {
      this.errorMsg = 'Please login to change password';
      return;
    }

    this._UserService.changePwd(this)
      .then((res: any) => {
        if (res.valid) {
          this._UserService.logout('logout').then(() => {
            this._router.navigate(['login'], {queryParams: {changePassMsg: res.validityMessage}});
          });
        } else {
          this.errorMsg = res.validityMessage;
        }
      });
  }

  cancel() {
    this._router.navigate(['login']);
  }
}
