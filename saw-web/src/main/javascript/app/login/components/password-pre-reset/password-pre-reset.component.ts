import { Component } from '@angular/core';
import { UserService } from '../../../common/services';
import { Router } from '@angular/router';

const template = require('./password-pre-reset.component.html');
require ('./password-pre-reset.component.scss');

@Component({
  selector: 'password-pre-reset',
  template
})

export class PasswordPreResetComponent {

  constructor(
    private _UserService: UserService,
    private _router: Router
    ) {}

  private dataHolder = {
    masterLoginId: null
  };

  private errorMsg;

  resetPwd() {
    this._UserService.preResetPwd(this.dataHolder)
    .then(res => {
      this.errorMsg = res.validityMessage;
    });
  }

  login() {
    this._router.navigate(['login']);
  }
}
