import { Component, OnInit } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../../../common/services';

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
  public username;
  public confNewPwd;
  public rhcToken;
  public newPwd;
  public showNewPassword: boolean;
  public showNewConfirmPassword: boolean;

  ngOnInit() {
    this.errorMsg = '';
    this.showNewPassword = false;
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

  resetPwd() {
    if (isEmpty(this.newPwd) || isEmpty(this.confNewPwd)) {
      this.errorMsg = 'Please enter all required fields';
    } else {
      this._UserService.resetPwd(this).then((res: any) => {
        this.errorMsg = res.validityMessage;
      });
    }
  }

  login() {
    this._router.navigate(['/#/login']);
  }

  toggleNewPassword() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleNewConfirmPassword() {
    this.showNewConfirmPassword = !this.showNewConfirmPassword;
  }
}
