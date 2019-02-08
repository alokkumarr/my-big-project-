import { Injectable, Compiler } from '@angular/core';
import { Route, Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { forEach } from 'lodash';
import * as AngularCore from '@angular/core';
import * as AngularRouting from '@angular/router';
import * as AngularAnimation from '@angular/animations';
import * as AngularCdk from '@angular/cdk';
import * as AngularCommon from '@angular/common';
import * as AngularCompiler from '@angular/compiler';
import * as AngularFlexLayout from '@angular/flex-layout';
import * as AngularForms from '@angular/forms';
import * as AngularHttp from '@angular/common/http';
import * as AngularMaterial from '@angular/material';
import * as AngularMaterialMomentAdapter from '@angular/material-moment-adapter';
import * as AngularBrowswer from '@angular/platform-browser';
import * as AngularBrowserDynamic from '@angular/platform-browser-dynamic';
import * as AngularSplit from 'angular-split';
import * as AngularCountTo from 'angular2-counto';
import * as CoreJs from 'core-js';
import * as DevextremeAngular from 'devextreme-angular';
import * as Ng2Nouslider from 'ng2-nouislider';
import * as Nouslider from 'nouislider';
import * as RxJsCompat from 'rxjs-compat';
import * as Lodash from 'lodash';
import * as LodashFp from 'lodash/fp';
import * as RxjsOperators from 'rxjs/operators';
import * as Rxjs from 'rxjs';
import * as mapboxgl from 'mapbox-gl';
import * as MapboxGeocoder from '@mapbox/mapbox-gl-geocoder';
import * as get from 'lodash/get';
import APP_CONFIG from '../../../../appConfig';

declare var SystemJS: any;
// prettier-ignore
const dependencies = {
  '@angular/core': AngularCore,
  '@angular/router': AngularRouting,
  '@angular/animations': AngularAnimation,
  '@angular/cdk': AngularCdk,
  '@angular/common': AngularCommon,
  '@angular/compiler': AngularCompiler,
  '@angular/flex-layout': AngularFlexLayout,
  '@angular/forms': AngularForms,
  '@angular/common/http': AngularHttp,
  '@angular/material': AngularMaterial,
  '@angular/material-moment-adapter': AngularMaterialMomentAdapter,
  '@angular/platform-browser': AngularBrowswer,
  '@angular/platform-browser-dynamic': AngularBrowserDynamic,
  'angular-split': AngularSplit,
  'angular2-counto': AngularCountTo,
  'core-js': CoreJs,
  'devextreme-angular': DevextremeAngular,
  'ng2-nouislider': Ng2Nouslider,
  'nouislider': Nouslider,
  'rxjs-compat': RxJsCompat,
  'rxjs': Rxjs,
  'rxjs/operators': RxjsOperators,
  'lodash': Lodash,
  'lodash/fp': LodashFp,
  'mapbox-gl': mapboxgl,
  '@mapbox/mapbox-gl-geocoder': MapboxGeocoder
};

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
  api = get(APP_CONFIG, 'api.pluginUrl');
  constructor(private compiler: Compiler, private router: Router) {
    this.existingRoutes$ = new BehaviorSubject<Route[]>(this.routes);
  }

  loadModuleSystemJs(moduleInfo: ModuleInfo) {
    // To fetch the plugin code, "moduleURL" should reflect just the umd file name without extensions.
    // Eg: If Insights plugin code is in "insights.umd.js", then moduleURL="insights"
    // Also it is expected that plugin code should be placed in saw/web folder in server

    const moduleServerURL = `${this.api}/${moduleInfo.moduleURL}.umd.js`;
    forEach(dependencies, (dep, key) =>
      SystemJS.set(key, SystemJS.newModule(dep))
    );

    // now, import the new module

    return new Promise((resolve, reject) => {
      SystemJS.import(moduleServerURL).then(
        module => {
          const mod = module[moduleInfo.moduleName];
          this.compiler.compileModuleAndAllComponentsAsync(mod).then(
            compiled => {
              this.createAndRegisterRoute(moduleInfo, module);
              resolve(true);
            },
            err => {
              reject(err);
            }
          );
        },
        err => {
          reject(err);
        }
      );
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
