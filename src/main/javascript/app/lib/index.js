import angular from 'angular';

import {CommonModule} from './common';
import {ComponentsModule} from './components';
import {DirectivesModule} from './directives';
import {FiltersModule} from './filters';

export const LibModule = 'LibModule';

angular
  .module(LibModule, [
    'ngSanitize',
    'pascalprecht.translate',
    FiltersModule,
    CommonModule,
    ComponentsModule,
    DirectivesModule,
    FiltersModule
  ]);
