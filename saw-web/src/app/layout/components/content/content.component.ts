import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import {
  Router,
  Event,
  RouteConfigLoadStart,
  RouteConfigLoadEnd,
  NavigationEnd,
  NavigationStart
} from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import * as includes from 'lodash/includes';
import * as split from 'lodash/split';
import * as replace from 'lodash/replace';
import * as startCase from 'lodash/startCase';
import * as upperCase from 'lodash/upperCase';

import {
  UserService,
  MenuService,
  HeaderProgressService
} from '../../../common/services';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../../../modules/admin/consts';

// nr of seconds before timeout starts
const TIMEOUT_TRIGGER = 60;
// nr of seconds before the user is timed out
const TIMEOUT = 20 * 60;

@Component({
  selector: 'layout-content',
  templateUrl: 'content.component.html'
})
export class LayoutContentComponent implements OnInit {
  title: string;
  isOnLoginPage = false;
  constructor(
    public _user: UserService,
    public _headerProgress: HeaderProgressService,
    public _router: Router,
    public _title: Title,
    public _sidenav: SidenavMenuService,
    public _menu: MenuService,
    public _idle: Idle
  ) {}

  ngOnInit() {
    this._router.events.subscribe((event: Event) => {
      if (event instanceof NavigationStart) {
        // remove the exclamation mark from the url
        const hasBang = includes(event.url, '!');
        if (hasBang) {
          const newUrl = replace(event.url, '!', '');
          this._router.navigateByUrl(newUrl);
        }
      } else if (event instanceof NavigationEnd) {
        this.isOnLoginPage = event.url.includes('login');
        this.setPageTitle(event);
        this.loadMenuForProperModule(event);
      } else if (event instanceof RouteConfigLoadStart) {
        this._headerProgress.show();
      } else if (event instanceof RouteConfigLoadEnd) {
        this._headerProgress.hide();
      }
    });
  }

  setUpIdleTimer() {
    this._idle.setIdle(TIMEOUT_TRIGGER);
    this._idle.setTimeout(TIMEOUT);
    this._idle.setInterrupts(DEFAULT_INTERRUPTSOURCES);

    this._idle.onTimeout.subscribe(() => {
      // log out user
      this._user.logout('logout').then(() => {
        this._router.navigate(['login']);
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

  setPageTitle(event) {
    const [, basePath] = split(event.url, '/');
    const moduleName = startCase(basePath);
    const title = `Synchronoss - ${moduleName}`;
    this._title.setTitle(title);
  }

  loadMenuForProperModule(event) {
    const [, basePath] = split(event.url, '/');
    const moduleName = upperCase(basePath);

    /* prettier-ignore */
    switch (moduleName) {
    case 'ADMIN':
      this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
      break;
    case 'OBSERVE':
      // it's handled in observe-page.component.ts
      break;
    case 'LOGIN':
      // do nothing
      break;
    default:
      this._menu.getMenu(moduleName).then(menu => {
        this._sidenav.updateMenu(menu, moduleName);
      });
      break;
    }
  }
}