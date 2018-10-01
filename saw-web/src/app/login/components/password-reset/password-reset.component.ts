import { Component, OnInit } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Router, ActivatedRoute } from '@angular/router';
import { UserService } from '../../../common/services';

@Component({
  selector: 'password-reset',
  templateUrl: 'password-reset.component.html'
})
export class PasswordResetComponent implements OnInit {
  constructor(
    private _UserService: UserService,
    private _router: Router,
    private _route: ActivatedRoute
  ) {}

  private errorMsg;
  private username;
  private confNewPwd;
  private newPwd;

  ngOnInit() {
    this._route.queryParams.subscribe(({ rhc }) => {
      const params = { rhc };
      this._UserService.verify(params).then((res: any) => {
        if (res.valid) {
          this.username = res.masterLoginID;
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
    this._router.navigate(['login']);
  }
}
