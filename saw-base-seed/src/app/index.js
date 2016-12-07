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
import {SidenavComponent, SidenavBtnComponent, SidenavTargetDirective} from './sidenav';

import {ObserveModule} from './observe';
import {AnalyzeModule} from './analyze';
import {CheckboxFilterComponent, RadioFilterComponent, TimeRangeFilterComponent, PriceRangeFilterComponent, FilterGroupComponent} from './observe/filters';
import {FilterSidenavComponent} from './observe/filter-sidenav.component';

// import app modules
import {LibModule} from './lib';

import './index.scss';

export const app = 'app';

angular
  .module(app, [
    'dx',
    'ngMaterial',
    'ui.router',
    'ui.grid',
    LibModule,
    AnalyzeModule,
    ObserveModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .component('root', RootComponent)
  .component('headerComponent', HeaderComponent)
  .component('footerComponent', FooterComponent)
  .component('home', HomeComponent)
  .directive('sidenavTarget', SidenavTargetDirective)
  .component('sidenav', SidenavComponent)
  .component('sidenavBtn', SidenavBtnComponent)
  .component('filterSidenav', FilterSidenavComponent)
  .component('checkboxFilter', CheckboxFilterComponent)
  .component('radioFilter', RadioFilterComponent)
  .component('timeRangeFilter', TimeRangeFilterComponent)
  .component('priceRangeFilter', PriceRangeFilterComponent)
  .component('filterGroup', FilterGroupComponent);
