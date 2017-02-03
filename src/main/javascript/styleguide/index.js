import angular from 'angular';
import 'angular-ui-router';

import 'angular-material';
import 'angular-material/angular-material.css';

import 'angular-ui-grid';
import 'angular-ui-grid/ui-grid.css';

import 'mottle';

import 'devextreme/ui/data_grid';
import 'devextreme/integration/angular';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import 'fonts/icomoon.css';

import {routesConfig} from './routes';
import {runConfig} from './run';
import {themeConfig} from '../app/theme';

import {LibModule} from '../app/lib';
import {SectionsModule} from './sections';

import {RootComponent} from './layout';

export const AppModule = 'styleguide';

angular.module(AppModule, [
  'dx',
  'ngMaterial',
  'ui.router',
  'ui.grid',
  LibModule,
  SectionsModule
])
  .config(routesConfig)
  .config(themeConfig)
  .run(runConfig)
  .component('root', RootComponent);
