import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import * as get from 'lodash/get';
import * as lowerCase from 'lodash/lowerCase';
import * as split from 'lodash/split';
import toMaterialStyle from 'material-color-hash';
import {
  JwtService,
  UserService,
  HeaderProgressService
} from '../../../common/services';

@Component({
  selector: 'layout-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class LayoutHeaderComponent implements OnInit, OnDestroy {
  public UserDetails: any;
  public modules: any;
  public showAdmin: boolean;
  public showProgress = false;
  public userInitials: string;
  public userBGColor: any;
  progressSub;
  lowerCase = lowerCase;

  constructor(
    public jwt: JwtService,
    public user: UserService,
    public _router: Router,
    public _headerProgress: HeaderProgressService
  ) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
  }

  ngOnInit() {
    this.UserDetails = this.jwt.getTokenObj();
    this.userInitials = this.getInitials(this.UserDetails.ticket.userFullName);
    const token = this.jwt.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    this.modules = product.productModules;
    if (this.jwt.isAdmin()) {
      this.showAdmin = true;
    }
  }

  ngOnDestroy() {
    this.progressSub.unsubscribe();
  }

  logout() {
    this.user.logout('logout').then(() => {
      this._router.navigate(['login']);
    });
  }

  changePwd() {
    this._router.navigate(['login', 'changePwd']);
  }

  getInitials(usrName: string) {
    const names = split(usrName, ' ');
    const initials = names[0].substring(0, 1).toUpperCase();

    // Below block gets you last name initial too
    // if (names.length > 1) {
    //   initials += names[names.length - 1].substring(0, 1).toUpperCase();
    // }
    this.userBGColor = toMaterialStyle(initials);
    return initials;
  }
}
