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
// import * as Devextreme from 'devextreme';
import * as DevextremeAngular from 'devextreme-angular';
import moment from 'moment';
import * as HighCharts from 'highcharts/highcharts';
import * as HighStock from 'highcharts/highstock';
import * as Ng2Nouslider from 'ng2-nouislider';
import * as Nouslider from 'nouislider';
import * as RxJsCompat from 'rxjs-compat';
import * as Lodash from 'lodash';
import * as LodashFp from 'lodash/fp';
import * as Rxjs from 'rxjs';
import * as RxjsOperators from 'rxjs/operators';

// import * as HighchartsMore from 'highcharts/highcharts-more';
// import * as HighchartsExporting from 'highcharts/modules/exporting';
// import * as HighchartsNoData from 'highcharts/modules/no-data-to-display';
// import * as HighchartsOfflineExporting from 'highcharts/modules/offline-exporting';
// import * as HighchartsDragPanes from 'highcharts/modules/drag-panes';
// import * as HighchartsBullet from 'highcharts/modules/bullet';
// import * as HighchartsBoost from 'highcharts/modules/boost';

const  HighchartsMore = require('highcharts/highcharts-more');
const HighchartsExporting = require('highcharts/modules/exporting');
const HighchartsNoData = require('highcharts/modules/no-data-to-display');
const HighchartsOfflineExporting = require('highcharts/modules/offline-exporting');
const HighchartsDragPanes = require('highcharts/modules/drag-panes');
const HighchartsBullet = require('highcharts/modules/bullet');
const HighchartsBoost = require('highcharts/modules/boost');

declare var SystemJS: any;

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
  // 'devextreme': Devextreme,
  'devextreme-angular': DevextremeAngular,
  'highcharts/highcharts': HighCharts,
  'highcharts/highstock': HighStock,
  'moment': moment,
  'ng2-nouislider': Ng2Nouslider,
  'nouislider': Nouslider,
  'rxjs-compat': RxJsCompat,
  'rxjs': Rxjs,
  'rxjs/operators': RxjsOperators,
  'lodash': Lodash,
  'lodash/fp': LodashFp,
  'highcharts/highcharts-more': HighchartsMore,
  'highcharts/modules/exporting': HighchartsExporting,
  'highcharts/modules/no-data-to-display': HighchartsNoData,
  'highcharts/modules/offline-exporting': HighchartsOfflineExporting,
  'highcharts/modules/drag-panes': HighchartsDragPanes,
  'highcharts/modules/bullet': HighchartsBullet,
  'highcharts/modules/boost': HighchartsBoost,
  // 'url': Url,
  // 'http': Http,
  // 'https': Https,
  // 'querystring': QueryString,
  // 'events': Events
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

  constructor(
    private compiler: Compiler,
    private router: Router
    ) {
      this.existingRoutes$ = new BehaviorSubject<Route[]>(this.routes);
    }

  loadModuleSystemJs(moduleInfo: ModuleInfo) {
    forEach(dependencies, (dep, key) => SystemJS.set(key, SystemJS.newModule(dep)));

    // now, import the new module

    return new Promise((resolve, reject) => {

      SystemJS.import(`${moduleInfo.moduleURL}`).then((module) => {
          const mod = module[moduleInfo.moduleName];
          this.compiler.compileModuleAndAllComponentsAsync(mod).then(compiled => {
            this.createAndRegisterRoute(moduleInfo, module);
            resolve(true);
        }, err => {
          reject(err);
        });
      }, err => {
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
