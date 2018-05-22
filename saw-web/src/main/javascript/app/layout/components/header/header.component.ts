import { Injectable, Component, Inject } from '@angular/core';
import * as get from 'lodash/get';
import { JwtService } from '../../../../login/services/jwt.service';
import { UserService } from '../../../../login/services/user.service';

const template = require('./header.component.html');
require('./header.component.scss');

@Component({
  selector: 'layout-header',
  template
})

@Injectable()
export class LayoutHeaderComponent {
  constructor(private jwt: JwtService, private user: UserService, @Inject('$rootScope') private _rootScope: any) { }

  public isLoading: false;

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
    this.isLoading = this._rootScope.showProgress;
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
