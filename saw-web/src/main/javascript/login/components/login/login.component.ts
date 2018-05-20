import { Injectable, Component, Inject } from '@angular/core';
import { JwtService } from '../../services/jwt.service';
import { UserService } from '../../services/user.service';
const template = require('./login.component.html');
require ('./login.component.scss');

@Component({
  selector: 'login',
  template
})

@Injectable()
export class LoginComponent {

  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

  private dataHolder = {
    username: null,
    password: null
  };

  private states = {
    error: null
  };
  
  login() {
    this._UserService.attemptAuth({masterLoginId: this.dataHolder.username,authpwd: this.dataHolder.password}).then(
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
    window.location.assign('preResetPwd');
  }
}
