import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import AppConfig from '../../../../../../appConfig';
import { Observable } from 'rxjs/Observable';
import { tap, flatMap } from 'rxjs/operators';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';

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

export const CONFIG_KEY = 'sipConfig';

@Injectable()
export class ConfigService {
  private cache: Configuration;

  constructor(private httpClient: HttpClient) {}

  private loadConfig(): Observable<Configuration> {
    return this.httpClient.get(
      `${AppConfig.login.url}/auth/admin/user/preferences/fetch`
    ) as Observable<Configuration>;
  }

  saveConfig(config: { key: string; value }[]): Observable<Configuration> {
    const payload: ConfigurationPreference[] = config.map(pref => ({
      preferenceName: pref.key,
      preferenceValue: pref.value
    }));

    return (this.httpClient.post(
      `${AppConfig.login.url}/auth/admin/user/preferences/upsert`,
      payload
    ) as Observable<Configuration>).pipe(tap(this.cacheConfig.bind(this)));
  }

  /**
   * Deletes config
   *
   * @config - Configuration to be deleted
   * @allUsers: boolean - whether this configuration needs to be deleted for all
   * users. Default: false
   *
   * @returns {undefined}
   */
  deleteConfig(
    config: { key: string; value }[],
    allUsers = false
  ): Observable<Configuration> {
    const payload: ConfigurationPreference[] = config.map(pref => ({
      preferenceName: pref.key,
      preferenceValue: pref.value
    }));

    return (this.httpClient.post(
      `${
        AppConfig.login.url
      }/auth/admin/user/preferences/delete?inactiveAll=${allUsers}`,
      payload
    ) as Observable<Configuration>).pipe(tap(this.removeCache.bind(this)));
  }

  private cacheConfig(config: Configuration) {
    window.localStorage.setItem(CONFIG_KEY, JSON.stringify(config));
    this.cache = config;
  }

  /**
   * removeCache - Removes supplied preferences from cache
   *
   * @param {Configuration} config
   * @returns {undefined}
   */
  private removeCache(config: Configuration) {
    const rawConfig = window.localStorage.getItem(CONFIG_KEY);
    const cachedConfig: Configuration = rawConfig
      ? JSON.parse(rawConfig)
      : null;

    cachedConfig.preferences = filter(cachedConfig.preferences, cachedPref => {
      return !find(
        config.preferences,
        pref => pref.preferenceName === cachedPref.preferenceName
      );
    });

    this.cacheConfig(cachedConfig);
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
    if (!this.cache) {
      const rawConfig = window.localStorage.getItem(CONFIG_KEY);
      this.cache = rawConfig ? JSON.parse(rawConfig) : null;
    }

    if (!this.cache) return null;

    const pref = find(
      this.cache.preferences,
      p => p.preferenceName === preferenceName
    );

    return pref ? pref.preferenceValue : null;
  }
}
