import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
import { Observable } from 'rxjs/Observable';
import { tap, flatMap } from 'rxjs/operators';
import * as find from 'lodash/find';

export interface ConfigurationPreference {
  preferenceName: string;
  preferenceValue: string;
}

export interface Configuration {
  userId: string;
  customerId: string;
  preferences: ConfigurationPreference[];
}

export const PREFERENCES = {
  DEFAULT_DASHBOARD: 'defaultDashboard',
  DEFAULT_DASHBOARD_CAT: 'defaultDashboardCategory'
};

const CONFIG_KEY = 'sipConfig';

@Injectable()
export class ConfigService {
  constructor(private httpClient: HttpClient) {}

  private loadConfig(): Observable<Configuration> {
    return this.httpClient.get(
      `${AppConfig.login.url}/auth/admin/user/preferences/fetch`
    ) as Observable<Configuration>;
  }

  saveConfig(config: [{ key: string; value }]): Observable<Configuration> {
    const payload: ConfigurationPreference[] = config.map(pref => ({
      preferenceName: pref.key,
      preferenceValue: pref.value
    }));

    return (this.httpClient.post(
      `${AppConfig.login.url}/auth/admin/user/preferences/upsert`,
      payload
    ) as Observable<Configuration>).pipe(tap(this.cacheConfig.bind(this)));
  }

  private cacheConfig(config: Configuration) {
    window.localStorage.setItem(CONFIG_KEY, JSON.stringify(config));
  }

  /**
   * getConfig Loads config from backend and caches it
   *
   * @returns {Configuration}
   */
  getConfig(): Observable<Configuration> {
    return this.loadConfig().pipe(tap(this.cacheConfig.bind(this)));
  }

  /**
   * getPreference Returns the value of a preference from saved config
   *
   * @param {string} preferenceName
   * @returns {string}
   */
  getPreference(preferenceName: string): string {
    const rawConfig = window.localStorage.getItem(CONFIG_KEY);
    const config = rawConfig ? JSON.parse(rawConfig) : null;

    if (!config) return null;

    const pref = find(
      config.preferences,
      p => p.preferenceName === preferenceName
    );

    return pref ? pref.preferenceValue : null;
  }
}
