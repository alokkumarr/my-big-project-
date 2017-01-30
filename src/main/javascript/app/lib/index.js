import angular from 'angular';
import {CommonModule} from './common';
import {ComponentsModule} from './components';
import {DirectivesModule} from './directives';
import {ServicesModule} from './services';

export const LibModule = 'LibModule';

angular
  .module(LibModule, [
    CommonModule,
    ServicesModule,
    ComponentsModule,
    DirectivesModule
  ]);
