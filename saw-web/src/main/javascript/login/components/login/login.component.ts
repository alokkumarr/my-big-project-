import { Component } from '@angular/core';
import { JwtService, UserService } from '../../../app/common/services';
import * as isUndefined from 'lodash/isUndefined';

const template = require('./login.component.html');
require ('./login.component.scss');

@Component({
  selector: 'login',
  template
})

export class LoginComponent {

  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

  private dataHolder = {
    username: null,
    password: null
  };

  private states = {
    error: null
  };

  ngOnInit() {
    const changePassMsg = window.location.href;
    if (!isUndefined(changePassMsg.split('changePassMsg=')[1])) {
      this.states.error = decodeURI(changePassMsg.split('changePassMsg=')[1]).split('#/')[0];
    }
  }

  login() {
    const params = {
      masterLoginId: this.dataHolder.username,
      authpwd: this.dataHolder.password
    };
    this._UserService.attemptAuth(params).then(
      data => {
        if (this._JwtService.isValid(data)) {
          window.location.assign('./');
        } else {
          this.states.error = this._JwtService.getValidityReason(data);
        }
      }
    );
  }

  reset() {
    window.location.assign(window.location.href + 'preResetPwd');
  }
}
