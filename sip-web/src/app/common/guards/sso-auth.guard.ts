import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { UserService } from '../services/user.service';

@Injectable()
export class SSOAuthGuard implements CanActivate {
  constructor(private router: Router, private userService: UserService) {}

  canActivate(route: ActivatedRouteSnapshot) {
    /* Redirect to invalid route. This will automatically kick in required
      guards for checking user auth and default module etc. */
    const invalidRoute = this.router.parseUrl('/abcinvalid');
    if (!route.queryParams || !route.queryParams.jwt) {
      return invalidRoute;
    } else {
      return this.userService.exchangeLoginToken(route.queryParams.jwt).pipe(
        map(() => invalidRoute),
        catchError(() => of(invalidRoute))
      );
    }
  }
}
