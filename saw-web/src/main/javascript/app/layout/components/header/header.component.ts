import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import * as get from 'lodash/get';
import { JwtService } from '../../../../login/services/jwt.service';
import { UserService } from '../../../../login/services/user.service';

const template = require('./header.component.html');
require('./header.component.scss');

@Component({
  selector: 'layout-header',
  template
})

export class LayoutHeaderComponent {
  constructor(
    private jwt: JwtService,
    private user: UserService,
    private cdRef:ChangeDetectorRef
  ) { }

  public isLoading: false;

  ngAfterViewChecked() {
    // if (this.isLoading === this._rootScope.showProgress) return;
    // this.isLoading = this._rootScope.showProgress;
    this.cdRef.detectChanges();
  }

  public UserDetails: any;
  public modules: any;
  public showAdmin: boolean;

  ngOnInit() {
    this.UserDetails = this.jwt.getTokenObj();
    const token = this.jwt.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    this.modules = product.productModules;
    if (this.jwt.isAdmin(token)) {
      this.showAdmin = true;
    }
  }

  get showProgress() {
    return this.isLoading;
  }

  logout() {
    this.user.logout('logout').then(() => {
      window.location.assign('./login.html');
    });
  }

  changePwd() {
    window.location.assign('./login.html#!/changePwd');
  }
}
