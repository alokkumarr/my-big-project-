import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import * as isEmpty from 'lodash/isEmpty';
import {
  JwtService,
  UserService,
  ConfigService
} from '../../../common/services';
import { BrandingService } from './../../../modules/admin/branding/branding.service';
import { DEFAULT_BRANDING_COLOR } from './../../../common/consts';

@Component({
  selector: 'login-form',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  @ViewChild('username', { static: true }) username: ElementRef;
  constructor(
    public _JwtService: JwtService,
    public _UserService: UserService,
    public _configService: ConfigService,
    public _router: Router,
    public _route: ActivatedRoute,
    public element: ElementRef<HTMLElement>,
    public _brandingService: BrandingService
  ) {}

  public dataHolder = {
    username: null,
    password: null
  };

  public state: boolean;

  public states = {
    error: null
  };

  ngOnInit() {
    this.states.error = '';
    this.state = true;
    this._route.queryParams.subscribe(({ changePassMsg }) => {
      if (changePassMsg) {
        this.states.error = changePassMsg;
      }
    });
    this._brandingService.savePrimaryColor(DEFAULT_BRANDING_COLOR);
  }

  login() {
    this.username.nativeElement.blur();
    if (isEmpty(this.dataHolder.password)) {
      this.username.nativeElement.blur();
    }
    if (
      isEmpty(this.dataHolder.username) ||
      isEmpty(this.dataHolder.password)
    ) {
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
        this.state = true;
        this._configService.getConfig().subscribe(
          () => {
            this._router.navigate(['']);
          },
          () => {
            this._router.navigate(['']);
          }
        );
      } else {
        this.state = false;
        this.username.nativeElement.blur();
        this.states.error = this._JwtService.getValidityReason(data);
      }
    });
  }

  reset() {
    this._router.navigate(['login', 'preResetPwd']);
  }

  passwordChanged(event) {
    this.dataHolder.password = event.target.value;
  }
}
