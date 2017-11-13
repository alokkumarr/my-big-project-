import { MenuService } from './menu.service';

export function menuServiceFactory(i: any) {
  return i.get('MenuService');
}

export const menuServiceProvider = {
  provide: MenuService,
  useFactory: menuServiceFactory,
  deps: ['$injector']
};
