import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../../../../fonts/style.css';
import 'devextreme/integration/angular';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import AppConfig from '../../../../appConfig';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {runConfig} from './run';

import {LibModule} from './lib';
import {ObserveModule} from './modules/observe';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';

import {HeaderComponent, RootComponent, FooterComponent} from './layout';

// import from login module
import {AuthServiceFactory} from '../login/services/auth.service';
import {UserServiceFactory} from '../login/services/user.service';
import {JwtServiceFactory} from '../login/services/jwt.service';

export const AppModule = 'app';

angular
  .module(AppModule, [
    'dx',
    'ngMaterial',
    'ui.router',
    'ui.grid',
    LibModule,
    ObserveModule,
    AnalyzeModule,
    AlertsModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .run(runConfig)
  .value('AppConfig', AppConfig)
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory)
  .component('root', RootComponent)
  .component('headerComponent', HeaderComponent)
  .component('footerComponent', FooterComponent);
