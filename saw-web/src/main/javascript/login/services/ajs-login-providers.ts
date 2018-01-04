import { JwtService } from './jwt.service';
import { UserService } from './user.service';

export function jwtServiceFactory(i: any) {
  return i.get('JwtService');
}

export const jwtServiceProvider = {
  provide: JwtService,
  useFactory: jwtServiceFactory,
  deps: ['$injector']
};

export function userServiceFactory(i: any) {
  return i.get('UserService');
}

export const userServiceProvider = {
  provide: UserService,
  useFactory: userServiceFactory,
  deps: ['$injector']
};
