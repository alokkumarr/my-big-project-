import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Router } from '@angular/router';
import toMaterialStyle from 'material-color-hash';
import {
  JwtService,
  UserService,
  HeaderProgressService
} from '../../../common/services';

import { split } from 'lodash';

@Component({
  selector: 'layout-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class LayoutHeaderComponent implements OnInit, OnDestroy {
  @Input() modules: any[];
  public UserDetails: any;
  public showAdmin: boolean;
  public showProgress = false;
  public userInitials: string;
  public userBGColor: any;
  progressSub;

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
    const token = this.jwt.getTokenObj();
    this.UserDetails = token;
    this.userInitials = this.getInitials(this.UserDetails.ticket.userFullName);
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
