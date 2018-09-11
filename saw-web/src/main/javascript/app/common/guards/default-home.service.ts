import { Router, CanActivate } from '@angular/router';
import { Injectable } from '@angular/core';

@Injectable()
export class DefaultHomePageGuard implements CanActivate {
  constructor(private router: Router) { }

  canActivate() {
    const config = localStorage.getItem('sipConfig');
    if (!config) {
      this.router.navigate(['/analyze']);
      return false;
    }

    try {
      const pref = JSON.parse(config);
      if (!Array.isArray(pref.preferences)) {
        this.router.navigate(['/analyze']);
        return false;
      }

      const defaultDashboard = pref.preferences.filter(p => p.preferenceName === 'defaultDashboard')[0];
      const defaultDashboardCat = pref.preferences.filter(p => p.preferenceName === 'defaultDashboardCategory')[0];

      if (!defaultDashboard || !defaultDashboardCat || !defaultDashboard.preferenceValue || !defaultDashboardCat.preferenceValue) {
        this.router.navigate(['/analyze']);
        return false;
      }

      this.router.navigate([`/observe`, defaultDashboardCat.preferenceValue], {
        queryParams: {
          dashboard: defaultDashboard.preferenceValue
        }
      })
      return false;
    } catch (err) {
      this.router.navigate(['/analyze']);
      return false;
    }
  }
}
