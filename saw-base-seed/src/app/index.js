import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../../fonts/style.css';
import 'devextreme/integration/angular';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import {routesConfig} from './routes';
import {themeConfig} from './theme';

import {RootComponent} from './layout/root.component';
import {HomeComponent} from './home.component';
import {HeaderComponent} from './layout/header.component';
import {FooterComponent} from './layout/footer.component';
import {SidenavComponent, SidenavBtnComponent} from './sidenav';

import {ObservePageComponent} from './observe/observe-page.component';
import {AnalyzePageComponent, newAnalysisService} from './analyze';

// import app modules
import {LibModule} from './lib';
import {loginModule} from '../login';

import './index.scss';

export const app = 'app';

angular
  .module(app, [
    'dx',
    'ngMaterial',
    'ui.router',
    'ui.grid',
    LibModule,
    loginModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .component('root', RootComponent)
  .component('headerComponent', HeaderComponent)
  .component('footerComponent', FooterComponent)
  .component('home', HomeComponent)
  .component('sidenav', SidenavComponent)
  .component('sidenavBtn', SidenavBtnComponent)
  .component('observePage', ObservePageComponent)
  .factory('newAnalysisService', newAnalysisService)
  .component('analyzePage', AnalyzePageComponent);
