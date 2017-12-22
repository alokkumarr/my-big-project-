import { MenuService } from './menu.service';

export function menuServiceFactory(i: any) {
  return i.get('MenuService');
}

export const menuServiceProvider = {
  provide: MenuService,
  useFactory: menuServiceFactory,
  deps: ['$injector']
};

export function stateFactory(i: any) {
  return i.get('$state');
}

export const stateProvider = {
  provide: '$state',
  useFactory: stateFactory,
  deps: ['$injector']
};

export function stateParamsFactory(i: any) {
  return i.get('$stateParams');
}

export const stateParamsProvider = {
  provide: '$stateParams',
  useFactory: stateParamsFactory,
  deps: ['$injector']
};

export function componentHandlerFactory(i: any) {
  return i.get('$componentHandler');
}

export const componentHandlerProvider = {
  provide: '$componentHandler',
  useFactory: componentHandlerFactory,
  deps: ['$injector']
};
