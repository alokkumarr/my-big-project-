import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../../../common/services';
import { validatePassword } from 'src/app/common/validators/password-policy.validator';

@Component({
  selector: 'password-reset',
  templateUrl: 'password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent implements OnInit {
  constructor(
    public _UserService: UserService,
    public _router: Router,
    public _route: ActivatedRoute
  ) {}

  public errorMsg;
  public newPasswordError: string;
  public confPasswordError: string;
  public username;
  public confNewPwd;
  public rhcToken;
  public newPwd;
  public passwordType = {
    NEWPASSWORD: 'New Password',
    CONFIRMPASSWORD: 'Confirm Password'
  };

  ngOnInit() {
    this.errorMsg = '';
    this._route.queryParams.subscribe(({ rhc }) => {
      const params = { rhc };
      this._UserService.verify(params).then((res: any) => {
        if (res.valid) {
          this.username = res.masterLoginID;
          this.rhcToken = rhc;
        } else {
          this.errorMsg =
            res.validityReason + '. Please regenerate the link once again';
        }
      });
    });
  }

  validatePassword() {
    this.newPasswordError = validatePassword(this.newPwd, this.username);
    this.confPasswordError =
      this.newPwd !== this.confNewPwd
        ? 'Needs to be same as new password.'
        : '';
  }

  resetPwd() {
    this.validatePassword();
    if (this.newPasswordError || this.confPasswordError) {
      return;
    } else {
      this._UserService.resetPwd(this).then((res: any) => {
        this.errorMsg = res.validityMessage;
      });
    }
  }

  login() {
    this._router.navigate(['/#/login']);
  }

  passwordChanged(event, type) {
    switch (type) {
      case this.passwordType.NEWPASSWORD:
        this.newPwd = event.target.value;
        break;

      case this.passwordType.CONFIRMPASSWORD:
        this.confNewPwd = event.target.value;
        break;
    }
  }
}
