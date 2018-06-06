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


// import * as template from './password-change.component.html';

// export const PasswordChangeComponent = {
//   template,
//   controller: class PasswordChangeController {
//     constructor($window, $state, JwtService, UserService) {
//       'ngInject';
//       this._$window = $window;
//       this._$state = $state;
//       this._JwtService = JwtService;
//       this._UserService = UserService;
//     }

//     changePwd() {
//       const token = this._JwtService.get();

//       if (!token) {
//         this.errorMsg = 'Please login to change password';
//       } else {
//         this._UserService.changePwd(this)
//           .then(res => {
//             this.errorMsg = res.data.validityMessage;
//           });
//       }
//     }

//     login() {
//       this._$state.go('login');
//     }
//   }
// };

