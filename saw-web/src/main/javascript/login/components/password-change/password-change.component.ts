import { Injectable, Component, Inject } from '@angular/core';
import { JwtService } from '../../services/jwt.service';
import { UserService } from '../../services/user.service';
const template = require('./password-change.component.html');
require ('./password-change.component.scss');

@Component({
  selector: 'password-change',
  template
})

@Injectable()
export class PasswordChangeComponent {

  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

  private formData = {
    oldPwd: null,
    newPwd: null,
    confNewPwd: null
  };

  private errorMsg;
  
  changePwd() {
    const token = this._JwtService.get();

    if (!token) {
      this.errorMsg = 'Please login to change password';
    } else {
      this._UserService.changePwd(this)
        .then(res => {
          this.errorMsg = res.data.validityMessage;
        });
    }
  }

  login() {
    window.location.assign('./');
  }
}
