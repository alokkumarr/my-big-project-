import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import '../fonts/style.css';
import 'devextreme/integration/angular';
import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import {routesConfig} from './routes';
import {themeConfig} from './theme';

import {RootComponent} from './app/layout/root.component';
import {HomeComponent} from './app/home.component';
import {HeaderComponent} from './app/layout/header.component';
import {FooterComponent} from './app/layout/footer.component';
import {SidenavComponent, SidenavBtnComponent} from './app/sidenav';

import {ObservePageComponent} from './app/observe/observe-page.component';
import {AnalyzePageComponent, newAnalysisService} from './app/analyze';

// import app modules
import {LibModule} from './app/lib';

import './index.scss';

export const app = 'app';

angular
  .module(app, [
    'dx',
    'ngMaterial',
    'ui.router',
    'ui.grid',
    LibModule
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
