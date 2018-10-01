import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

require('./password-change.component.scss');

@Component({
  selector: 'password-change',
  templateUrl: 'password-change.component.html'
})
export class PasswordChangeComponent {
  constructor(
    public _JwtService: JwtService,
    public _UserService: UserService,
    public _router: Router
  ) {}

  public formData = {
    oldPwd: null,
    newPwd: null,
    confNewPwd: null
  };

  public errorMsg;

  public formState: boolean;

  changePwd() {
    const token = this._JwtService.get();

    if (!token) {
      this.errorMsg = 'Please login to change password';
      return;
    }

    this._UserService.changePwd(this).then((res: any) => {
      if (res.valid) {
        this._UserService.logout('logout').then(() => {
          this._router.navigate(['login'], {
            queryParams: { changePassMsg: res.validityMessage }
          });
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
