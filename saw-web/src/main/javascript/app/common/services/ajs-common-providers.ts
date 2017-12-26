import { MenuService } from './menu.service';
import { HeaderProgressService } from './header-progress.service';

/* Menu Service */
export function menuServiceFactory(i: any) {
  return i.get('MenuService');
}

export const menuServiceProvider = {
  provide: MenuService,
  useFactory: menuServiceFactory,
  deps: ['$injector']
};

/* $state */
export function stateFactory(i: any) {
  return i.get('$state');
}

export const stateProvider = {
  provide: '$state',
  useFactory: stateFactory,
  deps: ['$injector']
};

/* $stateParams */
export function stateParamsFactory(i: any) {
  return i.get('$stateParams');
}

export const stateParamsProvider = {
  provide: '$stateParams',
  useFactory: stateParamsFactory,
  deps: ['$injector']
};

/* ComponentHandler Service */
export function componentHandlerFactory(i: any) {
  return i.get('$componentHandler');
}

export const componentHandlerProvider = {
  provide: '$componentHandler',
  useFactory: componentHandlerFactory,
  deps: ['$injector']
};

/* Header Progress Service */
export function headerProgressFactory(i: any) {
  return i.get('HeaderProgress');
}

export const headerProgressProvider = {
  provide: HeaderProgressService,
  useFactory: headerProgressFactory,
  deps: ['$injector']
};
