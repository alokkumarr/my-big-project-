import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, NavigationEnd, NavigationStart } from '@angular/router';
import { Idle, DEFAULT_INTERRUPTSOURCES } from '@ng-idle/core';
import * as includes from 'lodash/includes';
import * as split from 'lodash/split';
import * as replace from 'lodash/replace';
import * as startCase from 'lodash/startCase';
import * as upperCase from 'lodash/upperCase';

import { UserService, MenuService} from '../../../common/services';
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
export class LayoutContentComponent implements OnInit {
  title: string;

  constructor(
    private _user: UserService,
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

  ngOnInit() {
    this._router.events.subscribe(event => {
      if (event instanceof NavigationStart) {
        // remove the exclamation mark from the url
        const hasBang = includes(event.url, '!');
        if (hasBang) {
          const newUrl = replace(event.url, '!', '');
          this._router.navigateByUrl(newUrl);
        }
      } else if (event instanceof NavigationEnd) {
        this.setPageTitle(event);
        this.loadMenuForProperModule(event);
      }
    });
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
