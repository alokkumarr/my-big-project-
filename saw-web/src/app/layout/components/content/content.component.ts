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
import {
  map,
  filter,
  forEach,
  lowerCase,
  startCase,
  upperCase,
  replace,
  split,
  includes,
  get,
  once
} from 'lodash';
import {
  UserService,
  MenuService,
  JwtService,
  HeaderProgressService,
  DynamicModuleService
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
  isUserLoggedIn = false;
  public modules: any[];
  constructor(
    public _user: UserService,
    public _headerProgress: HeaderProgressService,
    public _router: Router,
    public _title: Title,
    public _sidenav: SidenavMenuService,
    public _menu: MenuService,
    public _idle: Idle,
    public jwt: JwtService,
    private _dynamicModuleService: DynamicModuleService
  ) {}

  ngOnInit() {
    const loadExternalModulesOnce = once(() => this.loadExternalModules());

    this._router.events.subscribe((event: Event) => {
      if (event instanceof NavigationStart) {
        this.removeExclamationMarkFromUrl(event);
      } else if (event instanceof NavigationEnd) {
        this.isUserLoggedIn = this._user.isLoggedIn();
        this.setPageTitle(event);
        this.loadMenuForProperModule(event);
        if (this.isUserLoggedIn) {
          loadExternalModulesOnce();
        }
      } else if (event instanceof RouteConfigLoadStart) {
        this._headerProgress.show();
      } else if (event instanceof RouteConfigLoadEnd) {
        this._headerProgress.hide();
      }
    });
  }

  loadExternalModules() {
    const token = this.jwt.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    const baseModuleNames = ['ANALYZE', 'OBSERVE', 'WORKBENCH'];
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
    const baseModules = filter(modules, ({ label }) => baseModuleNames.includes(label));
    const externalModules = filter(
      modules,
      ({ label }) => !baseModuleNames.includes(label)
    );

    this.modules = baseModules;
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

  removeExclamationMarkFromUrl(event) {
    // remove the exclamation mark from the url
    const hasBang = includes(event.url, '!');
    if (hasBang) {
      const newUrl = replace(event.url, '!', '');
      this._router.navigateByUrl(newUrl);
    }
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
