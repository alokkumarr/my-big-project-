import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../../../../fonts/style.css';
import 'devextreme/integration/angular';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {LibModule} from './lib';
import {LoginModule} from './modules/login';
import {ObserveModule} from './modules/observe';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';

import {HeaderComponent, RootComponent, FooterComponent} from './layout';

import AppConfig from '../../../../appConfig';

export const AppModule = 'app';

angular
  .module(AppModule, [
    'dx',
    'ngMaterial',
    'ui.router',
    'ui.grid',
    LibModule,
    LoginModule,
    ObserveModule,
    AnalyzeModule,
    AlertsModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .value('AppConfig', AppConfig)
  .component('root', RootComponent)
  .component('headerComponent', HeaderComponent)
  .component('footerComponent', FooterComponent);
