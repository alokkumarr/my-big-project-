import angular from 'angular';
import {CommonModule} from './common';
import {ComponentsModule} from './components';
import {DirectivesModule} from './directives';

export const LibModule = 'LibModule';

angular
  .module(LibModule, [
    CommonModule,
    ComponentsModule,
    DirectivesModule
  ]);
