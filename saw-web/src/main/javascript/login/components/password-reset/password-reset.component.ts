import { Injectable, Component, Inject } from '@angular/core';
import { JwtService } from '../../services/jwt.service';
import { UserService } from '../../services/user.service';
const template = require('./password-reset.component.html');

@Component({
  selector: 'password-reset',
  template
})

@Injectable()
export class PasswordResetComponent {
  constructor(private _JwtService: JwtService, private _UserService: UserService) {}

  private errorMsg;

  ngOnInit() {
    if (window.location.href.indexOf('/resetPassword?rhc') !== -1) {
      const hashCode = window.location.href;
      const rhc = hashCode.split('rhc=')[1];
      const rData = {
        rhc
      };
      this._UserService.verify(rData).then(res => {
        if (res.data.valid) {
          this.username = res.data.masterLoginID;
        } else {
          this.errorMsg = res.data.validityReason + '. Please regenerate the link once again';
        }
      });
    }
  }

  resetPwd() {
    this._UserService.resetPwd(this)
      .then(res => {
        this.errorMsg = res.data.validityMessage;
      });
  }

  login() {
    window.location.assign('./login');
  }
}