import { Component } from '@angular/core';
import { JwtService, UserService } from '../../../app/common/services';

const template = require('./password-pre-reset.component.html');
require ('./password-pre-reset.component.scss');

@Component({
  selector: 'password-pre-reset',
  template
})

export class PasswordPreResetComponent {

  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

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
    window.location.assign('./');
  }
}
