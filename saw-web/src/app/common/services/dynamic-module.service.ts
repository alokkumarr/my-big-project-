import { Injectable, Compiler } from '@angular/core';
import { Route, Router } from '@angular/router';
import * as AngularCore from '@angular/core';
import * as AngularRouting from '@angular/router';
import { BehaviorSubject } from 'rxjs';

declare var SystemJS: any;

interface ModuleInfo {
  path: string;
  name: string;
  label: string;
  moduleName: string;
  moduleURL: string;
}

@Injectable()
export class DynamicModuleService {
  existingRoutes$: BehaviorSubject<Route[]>;

  constructor(
    private compiler: Compiler,
    private router: Router
    ) {
      this.existingRoutes$ = new BehaviorSubject<Route[]>(this.routes);
    }

  loadModuleSystemJs(moduleInfo: ModuleInfo) {
    SystemJS.set('@angular/core', SystemJS.newModule(AngularCore));
    SystemJS.set('@angular/router', SystemJS.newModule(AngularRouting));

    // now, import the new module

    return new Promise((resolve, reject) => {

      SystemJS.import(`${moduleInfo.moduleURL}`).then((module) => {
          const mod = module[moduleInfo.moduleName];
          this.compiler.compileModuleAndAllComponentsAsync(mod).then(compiled => {
            this.createAndRegisterRoute(moduleInfo, module);
            console.log('resolved');
            resolve(true);
        }, err => {
          console.log('rejeted1', err);
          reject(err);
        });
      }, err => {
        console.log('rejeted2', err);
        reject(err);
      });
    });
  }

  private get routes(): Route[] {
    const routesToReturn = this.router.config;
    return routesToReturn.filter(x => x.path !== '');
  }

  createAndRegisterRoute(moduleToRegister, module: any) {
    const route: Route = {
        path: moduleToRegister.path,
        loadChildren: () => module[`${moduleToRegister.moduleName}`]
    };

    this.registerRoute(route);
  }

  registerRoute(route: Route) {
    if (this.routeIsRegistered(route.path)) {
      return;
    }

    this.router.config.unshift(route);
    console.log('routes', this.router.config);
    this.updateRouteConfig(this.router.config);
  }

  routeIsRegistered(path: string) {
    return this.router.config.filter(r => r.path === path).length > 0;
  }

  private updateRouteConfig(config) {
    this.router.resetConfig(config);
    this.existingRoutes$.next(this.routes);
  }
}
