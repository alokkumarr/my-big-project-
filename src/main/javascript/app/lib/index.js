import angular from 'angular';

import 'angular-translate';
import 'angular-translate/dist/angular-translate-loader-static-files/angular-translate-loader-static-files';

import {CommonModule} from './common';
import {ComponentsModule} from './components';
import {DirectivesModule} from './directives';

export const LibModule = 'LibModule';

angular
  .module(LibModule, [
    'pascalprecht.translate',
    CommonModule,
    ComponentsModule,
    DirectivesModule
  ]);
