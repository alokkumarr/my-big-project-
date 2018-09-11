import { Component } from '@angular/core';
import { JwtService } from '../../services/jwt.service';
import { UserService } from '../../services/user.service';
import * as isUndefined from 'lodash/isUndefined';
import { ConfigService } from '../../../app/common/services/configuration.service';

const template = require('./login.component.html');
require('./login.component.scss');

@Component({
  selector: 'login',
  template
})
export class LoginComponent {
  constructor(
    private _JwtService: JwtService,
    private _UserService: UserService,
    private configService: ConfigService
  ) {}

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
      this.states.error = decodeURI(
        changePassMsg.split('changePassMsg=')[1]
      ).split('#!/')[0];
    }
  }

  login() {
    this._UserService
      .attemptAuth({
        masterLoginId: this.dataHolder.username,
        authpwd: this.dataHolder.password
      })
      .then(data => {
        if (this._JwtService.isValid(data)) {
          this.configService
            .getConfig()
            .subscribe(
              () => window.location.assign('./'),
              () => window.location.assign('./')
            );
        } else {
          this.states.error = this._JwtService.getValidityReason(data);
        }
      });
  }

  reset() {
    window.location.assign(window.location.href + 'preResetPwd');
  }
}
