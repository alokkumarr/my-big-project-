import { Component } from '@angular/core';
import { UserService } from '../../../common/services';
import { Router } from '@angular/router';

require('./password-pre-reset.component.scss');

@Component({
  selector: 'password-pre-reset',
  templateUrl: 'password-pre-reset.component.html'
})
export class PasswordPreResetComponent {
  constructor(private _UserService: UserService, public _router: Router) {}

  public dataHolder = {
    masterLoginId: null
  };

  public errorMsg;

  resetPwd() {
    this._UserService.preResetPwd(this.dataHolder).then((res: any) => {
      this.errorMsg = res.validityMessage;
    });
  }

  login() {
    this._router.navigate(['login']);
  }
}
