import template from './login.component.html';

export const LoginComponent = {
  template,
  controller: class LoginController {
    constructor($window, UserService) {
      'ngInject';
      this._$window = $window;
      this._UserService = UserService;

      this.dataHolder = {
        username: null,
        password: null
      };

      this.states = {
        error: null
      };
    }

    login() {
      this._UserService.attemptAuth({
        masterLoginId: this.dataHolder.username,
        authpwd: this.dataHolder.password
      })
        .then(res => {
          if (res.ticket.valid) {
            const baseUrl = this._$window.location.origin;
            //this._$window.location = baseUrl+'/app.html';
        	this._UserService.redirect('/app.html')
            .then(res => {
            	this._$window.location = baseUrl+res.data.validityMessage;            	
            });
          } else {
            this.states.error = res.ticket.validityReason;
          }
        })
        .catch(() => {
          this.states.error = 'Network Error!';
        });
    }
    
    reset() {
        const baseUrl = this._$window.location.origin;
        const appUrl = `${baseUrl}/login.html#!/preResetPwd`;
        this._$window.location = appUrl; 
      }
  }
};
