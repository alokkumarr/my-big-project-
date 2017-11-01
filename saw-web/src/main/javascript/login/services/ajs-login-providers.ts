import { JwtService } from './jwt.service';

export function jwtServiceFactory(i: any) {
  return i.get('JwtService');
}

export const jwtServiceProvider = {
  provide: JwtService,
  useFactory: jwtServiceFactory,
  deps: ['$injector']
};
