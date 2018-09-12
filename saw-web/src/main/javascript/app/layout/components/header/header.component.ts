import { Component, Inject, ChangeDetectorRef } from '@angular/core';
import * as get from 'lodash/get';
import * as lowerCase from 'lodash/lowerCase';
import * as split from 'lodash/split';
import toMaterialStyle from 'material-color-hash';
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
    private cdRef: ChangeDetectorRef
  ) {}

  public isLoading: false;
  lowerCase = lowerCase;

  ngAfterViewChecked() {
    // if (this.isLoading === this._rootScope.showProgress) return;
    // this.isLoading = this._rootScope.showProgress;
    this.cdRef.detectChanges();
  }

  public UserDetails: any;
  public modules: any;
  public showAdmin: boolean;
  private userInitials: string;
  private userBGColor: any;

  ngOnInit() {
    this.UserDetails = this.jwt.getTokenObj();
    this.userInitials = this.getInitials(this.UserDetails.ticket.userFullName);
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

  getInitials(usrName: string) {
    const names = split(usrName, ' ');
    let initials = names[0].substring(0, 1).toUpperCase();

    //Below block gets you last name initial too
    // if (names.length > 1) {
    //   initials += names[names.length - 1].substring(0, 1).toUpperCase();
    // }
    this.userBGColor = toMaterialStyle(initials);
    return initials;
  }
}
