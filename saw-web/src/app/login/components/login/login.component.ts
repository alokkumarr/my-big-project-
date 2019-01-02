import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import * as isEmpty from 'lodash/isEmpty';
import {
  JwtService,
  UserService,
  ConfigService
} from '../../../common/services';

@Component({
  selector: 'login-form',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  constructor(
    public _JwtService: JwtService,
    public _UserService: UserService,
    public _configService: ConfigService,
    public _router: Router,
    public _route: ActivatedRoute
  ) {}

  public dataHolder = {
    username: null,
    password: null
  };

  public states = {
    error: null
  };

  ngOnInit() {
    this.states.error = '';
    this._route.queryParams.subscribe(({ changePassMsg }) => {
      if (changePassMsg) {
        this.states.error = changePassMsg;
      }
    });
  }

  login() {
    if (isEmpty(this.dataHolder.username) || isEmpty(this.dataHolder.password)) {
      this.states.error = 'Please enter a valid Username and Password';
      return false;
    }
    const params = {
      masterLoginId: this.dataHolder.username,
      authpwd: this.dataHolder.password
    };
    this._UserService.attemptAuth(params).then(data => {
      this.states.error = '';
      if (this._JwtService.isValid(data)) {
        this._configService.getConfig().subscribe(
          () => {
            this._router.navigate(['']);
          },
          () => {
            this._router.navigate(['']);
          }
        );
      } else {
        this.states.error = this._JwtService.getValidityReason(data);
      }
    });
  }

  reset() {
    this._router.navigate(['login', 'preResetPwd']);
  }
}
