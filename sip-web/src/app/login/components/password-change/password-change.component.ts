import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

@Component({
  selector: 'password-change',
  templateUrl: 'password-change.component.html',
  styleUrls: ['./password-change.component.scss']
})
export class PasswordChangeComponent implements OnInit {
  public showOldPassword: boolean;
  public showNewPassword: boolean;
  public showNewConfirmPassword: boolean;
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

  ngOnInit() {
    this.showOldPassword = false;
    this.showNewPassword = false;
    this.showNewConfirmPassword = false;
  }

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

  toggleOldPassword() {
    this.showOldPassword = !this.showOldPassword;
  }

  toggleNewPassword() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleNewConfirmPassword() {
    this.showNewConfirmPassword = !this.showNewConfirmPassword;
  }
}
