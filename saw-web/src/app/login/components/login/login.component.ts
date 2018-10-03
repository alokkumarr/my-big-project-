import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { JwtService, UserService } from '../../../common/services';

@Component({
  selector: 'login-form',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  constructor(
    public _JwtService: JwtService,
    public _UserService: UserService,
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
    this._route.queryParams.subscribe(({ changePassMsg }) => {
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
    this._UserService.attemptAuth(params).then(data => {
      if (this._JwtService.isValid(data)) {
        this._router.navigate(['']);
      } else {
        this.states.error = this._JwtService.getValidityReason(data);
      }
    });
  }

  reset() {
    this._router.navigate(['login', 'preResetPwd']);
  }
}
