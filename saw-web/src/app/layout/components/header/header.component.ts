import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import toMaterialStyle from 'material-color-hash';
import {
  JwtService,
  UserService,
  HeaderProgressService,
  DynamicModuleService
} from '../../../common/services';

import { map, filter, forEach, lowerCase, startCase, split, get } from 'lodash';

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

  constructor(
    public jwt: JwtService,
    public user: UserService,
    public _router: Router,
    public _headerProgress: HeaderProgressService,
    private _dynamicModuleService: DynamicModuleService
  ) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
  }

  ngOnInit() {
    const token = this.jwt.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    this.UserDetails = token;
    this.userInitials = this.getInitials(this.UserDetails.ticket.userFullName);
    if (this.user.isLoggedIn()) {
      this.setModules(product);
    }
    if (this.jwt.isAdmin()) {
      this.showAdmin = true;
    }
  }

  setModules(product) {
    const baseModules = ['ANALYZE', 'OBSERVE', 'WORKBENCH'];
    const modules = map(
      product.productModules,
      ({ productModName, moduleURL }) => {
        const lowerCaseName = lowerCase(productModName);
        return {
          label: productModName,
          path: lowerCaseName,
          name: lowerCaseName,
          moduleName: `${startCase(lowerCaseName)}Module`,
          moduleURL
        };
      }
    );

    this.modules = filter(modules, ({ label }) => baseModules.includes(label));

    const externalModules = filter(
      modules,
      ({ label }) => !baseModules.includes(label)
    );

    forEach(externalModules, externalModule => {
      this._dynamicModuleService.loadModuleSystemJs(externalModule).then(
        success => {
          if (success) {
            this.modules = [...this.modules, externalModule];
          }
        },
        err => {
          console.error(err);
        }
      );
    });
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
