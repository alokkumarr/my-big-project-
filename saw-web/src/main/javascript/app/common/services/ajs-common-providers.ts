import { MenuService } from './menu.service';
import { HeaderProgressService } from './header-progress.service';
import { ToastService } from './toastMessage.service';
import { SideNavService } from './sidenav.service';

/* Menu Service */
export function menuServiceFactory(i: any) {
  return i.get('MenuService');
}

export const menuServiceProvider = {
  provide: MenuService,
  useFactory: menuServiceFactory,
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

/* Toast Service */
export function toastFactory(i: any) {
  return i.get('toastMessage');
}

export const toastProvider = {
  provide: ToastService,
  useFactory: toastFactory,
  deps: ['$injector']
};

/* Sidenav Service */
export function sidenavFactory(i: any) {
  return i.get('SidenavService');
}

export const sidenavProvider = {
  provide: SideNavService,
  useFactory: sidenavFactory,
  deps: ['$injector']
};

/* Dialog Service */
export function $mdDialogFactory(i: any) {
  return i.get('$mdDialog');
}

export const $mdDialogProvider = {
  provide: '$mdDialog',
  useFactory: $mdDialogFactory,
  deps: ['$injector']
};
