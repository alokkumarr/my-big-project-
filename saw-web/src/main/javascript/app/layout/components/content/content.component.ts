import { Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd } from '@angular/router';
import {Idle, DEFAULT_INTERRUPTSOURCES} from '@ng-idle/core';
import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as split from 'lodash/split';
import * as startCase from 'lodash/startCase';
import * as upperCase from 'lodash/upperCase';

import { JwtService } from '../../../../login/services/jwt.service';
import { UserService } from '../../../../login/services/user.service';
import { MenuService } from '../../../common/services/menu.service';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../../../modules/admin/consts';

const template = require('./content.component.html');

// nr of seconds before timeout starts
const TIMEOUT_TRIGGER = 60;
// nr of seconds before the user is timed out
const TIMEOUT = 20 * 60;

@Component({
  selector: 'layout-content',
  template
})
export class LayoutContentComponent {
  title: string;

  constructor(
    private _user: UserService,
    private _jwt: JwtService,
    private _router: Router,
    private _title: Title,
    private _sidenav: SidenavMenuService,
    private _menu: MenuService,
    private _idle: Idle
  ) {}

  setUpIdleTimer() {
    this._idle.setIdle(TIMEOUT_TRIGGER);
    this._idle.setTimeout(TIMEOUT);
    this._idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    this._idle.onTimeout.subscribe(() => {
      // log out user
      this._user.logout('logout').then(() => {
        window.location.assign('./login.html');
      });
    });
    this._idle.onIdleStart.subscribe(() => {
      this.title = this._title.getTitle();
    });
    this._idle.onTimeoutWarning.subscribe(countdown => {
      const minutes = Math.floor(countdown / 60);
      const seconds = countdown % 60;
      this._title.setTitle(`Log out in ${minutes}:${seconds}`);
    });

    this._idle.onInterrupt.subscribe(() => {
      this._title.setTitle(this.title);
    });
    this._idle.watch();
  }

  ngOnInit() {
    this._router.events
      .subscribe(event => {
        if (event instanceof NavigationEnd) {
          this.setPageTitle(event);
        }
      });
    this.goToAnalyzePage();
  }

  setPageTitle(event) {
    const [, basePath] = split(event.url, '/');
    const title = startCase(basePath);
    this._title.setTitle(title);
  }

  loadMenuForProperModule(event) {
    const [, basePath] = split(event.url, '/');
    const moduleName = upperCase(basePath);

    switch (moduleName) {
    case 'ADMIN':
      this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
      break;
    case 'OBSERVE':
      // it's handled in observe-page.component.ts
      break;
    default:
      this._menu.getMenu(moduleName).then(menu => {
        this._sidenav.updateMenu(menu, moduleName);
      });
      break;
    }
  }

  goToAnalyzePage() {
    const analyzeModuleName = 'ANALYZE';
    const modules = get(this._jwt.getTokenObj(), 'ticket.products[0].productModules');
    const analyzeModuleExists = find(modules, ({productModName}) => productModName === analyzeModuleName);

    if (analyzeModuleExists) {
      this._router.navigate(['analyze']);
    }
  }
}
