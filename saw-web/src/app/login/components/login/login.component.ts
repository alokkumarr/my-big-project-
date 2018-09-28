import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

const style = require ('./login.component.scss');

@Component({
  selector: 'login-form',
  templateUrl: './login.component.html',
  styles: [style]
})

export class LoginComponent implements OnInit {

  constructor(
    private _JwtService: JwtService,
    private _UserService: UserService,
    private _router: Router,
    private _route: ActivatedRoute
    ) {}

  private dataHolder = {
    username: null,
    password: null
  };

  private states = {
    error: null
  };

  ngOnInit() {
    this._route.queryParams.subscribe(({changePassMsg}) => {
      if (changePassMsg) {
        this.states.error = changePassMsg;
      }
    });
  }

  login() {
    const params = {
      masterLoginId: this.dataHolder.username,
      authpwd: this.dataHolder.password
    };
    this._UserService.attemptAuth(params).then(
      data => {
        if (this._JwtService.isValid(data)) {
          this._router.navigate(['']);
        } else {
          this.states.error = this._JwtService.getValidityReason(data);
        }
      }
    );
  }

  reset() {
    this._router.navigate(['login', 'preResetPwd']);
  }
}
