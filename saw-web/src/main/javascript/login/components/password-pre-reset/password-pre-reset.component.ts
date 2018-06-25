import { Injectable, Component } from '@angular/core';
import { JwtService } from '../../services/jwt.service';
import { UserService } from '../../services/user.service';
import * as isEmpty from 'lodash/isEmpty';

const template = require('./password-pre-reset.component.html');
require ('./password-pre-reset.component.scss');

@Component({
  selector: 'password-pre-reset',
  template
})

@Injectable()
export class PasswordPreResetComponent {

  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

  private dataHolder = {
    masterLoginId: null
  };

  private errorMsg;
  
  resetPwd() {
    this._UserService.preResetPwd(this.dataHolder)
    .then(res => {
      this.errorMsg = res.data.validityMessage;
    });
  }

  login() {
    window.location.assign('./');
  }
}
