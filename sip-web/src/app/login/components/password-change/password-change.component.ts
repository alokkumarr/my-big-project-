import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

@Component({
  selector: 'password-change',
  templateUrl: 'password-change.component.html',
  styleUrls: ['./password-change.component.scss']
})
export class PasswordChangeComponent {
  constructor(
    public _JwtService: JwtService,
    public _UserService: UserService,
    public _router: Router
  ) {}

  public passwordType = {
    OLDPASSWORD: 'Old Password',
    NEWPASSWORD: 'New Password',
    CONFIRMPASSWORD: 'Confirm Password'
  };

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

  onPasswordChanged(event, type) {
    switch (type) {
      case this.passwordType.OLDPASSWORD:
        this.formData.oldPwd = event.target.value;
        break;

      case this.passwordType.NEWPASSWORD:
        this.formData.newPwd = event.target.value;
        break;

      case this.passwordType.CONFIRMPASSWORD:
        this.formData.confNewPwd = event.target.value;
        break;
    }
  }
}
