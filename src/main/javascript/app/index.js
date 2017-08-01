import angular from 'angular';

import 'angular-material/angular-material.css';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import 'fonts/icomoon.css';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {i18nConfig} from './i18n';
import {config} from './config';
import {interceptor} from './http-interceptor';
import {runConfig} from './run';

import {ObserveModule} from './modules/observe';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';
import {AdminModule} from './modules/admin';

import {LayoutHeaderComponent, LayoutContentComponent, LayoutFooterComponent} from './layout';

export const AppModule = 'app';

angular
  .module(AppModule, [
    ObserveModule,
    AnalyzeModule,
    AlertsModule,
    AdminModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .config(i18nConfig)
  .config(config)
  .config(interceptor)
  .run(runConfig)
  .component('layoutHeader', LayoutHeaderComponent)
  .component('layoutContent', LayoutContentComponent)
  .component('layoutFooter', LayoutFooterComponent);
