import * as get from 'lodash/get';
import * as has from 'lodash/has';
import * as isArray from 'lodash/isArray';
import * as padStart from 'lodash/padStart';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import AppConfig from '../../../../../appConfig';
import { Injectable } from '@angular/core';

const PRIVILEGE_CODE_LENGTH = 16;

const PRIVILEGE_INDEX = {
  ACCESS: 0,
  CREATE: 1,
  EXECUTE: 2,
  PUBLISH: 3,
  FORK: 4,
  EDIT: 5,
  EXPORT: 6,
  DELETE: 7,
  ALL: 8
};

/* Jwt token can have custom config values to drive
 * behaviors in app. */
export const CUSTOM_JWT_CONFIG = {
  ES_ANALYSIS_AUTO_REFRESH: 'es-analysis-auto-refresh'
};

@Injectable()
export class JwtService {
  _refreshTokenKey = `${AppConfig.login.jwtKey}Refresh`;

  set(accessToken, refreshToken) {
    window.localStorage[AppConfig.login.jwtKey] = accessToken;
    window.localStorage[this._refreshTokenKey] = refreshToken;
  }

  get() {
    return window.localStorage[AppConfig.login.jwtKey];
  }

  getCategories(moduleName = 'ANALYZE') {
    const token = this.getTokenObj();
    const analyzeModule = find(
      get(token, 'ticket.products[0].productModules'),
      mod => mod.productModName === moduleName
    );

    return get(analyzeModule, 'prodModFeature', []) || [];
  }

  /**
   * hasCustomConfig
   * Checks whether the current access token has
   * a particular custom configuration enabled or not.
   *
   * @param configName
   * @returns {undefined}
   */
  hasCustomConfig(configName) {
    const customConfig = get(this.getTokenObj(), 'ticket.customConfig') || [];
    return isArray(customConfig) ?
      customConfig.includes(configName) :
      false;
  }

  getAccessToken() {
    return this.get();
  }

  getRefreshToken() {
    return window.localStorage[this._refreshTokenKey];
  }

  validity() {
    return new Date(this.getTokenObj().ticket.validUpto);
  }

  destroy() {
    window.localStorage.removeItem(AppConfig.login.jwtKey);
    window.localStorage.removeItem(this._refreshTokenKey);
  }

  parseJWT(jwt) {
    if (!jwt) {
      return null;
    }
    const base64Url = jwt.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64));
  }

  /* Returs the parsed json object from the jwt token */
  getTokenObj() {
    const token = this.get();

    if (!token) {
      return null;
    }

    return this.parseJWT(this.get());
  }

  get customerCode() {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.custCode', 'ATT');
  }

  isValid(token) {
    return (
      get(token, 'ticket.valid', false) &&
      get(token, 'ticket.validUpto', 0) >= Date.now()
    );
  }

  /* Bootstraps request structure with necessary auth data */
  getRequestParams() {
    const token = this.getTokenObj();
    return {
      contents: {
        keys: [
          {
            customerCode: get(token, 'ticket.custCode', 'ATT')
            // dataSecurityKey: get(token, 'ticket.dataSecurityKey')
          }
        ]
      }
    };
  }

  getValidityReason(token = this.getTokenObj()) {
    return token.ticket.validityReason;
  }

  getUserId() {
    return get(this.getTokenObj(), 'ticket.userId').toString();
  }

  _isRole(token, role) {
    const roleType = get(token, 'ticket.roleType');
    return roleType === role;
  }

  isAdmin(token) {
    return this._isRole(token, 'ADMIN');
  }

  isOwner(token, creatorId) {
    creatorId = creatorId || '';
    return creatorId.toString() === get(token, 'ticket.userId').toString();
  }

  _isSet(code, bitIndex) {
    const fullCode = padStart(
      (code >>> 0).toString(2),
      PRIVILEGE_CODE_LENGTH,
      '0'
    );
    /* If index of 'All' privileges is set, it is considered same as if the
       requested privilege bit is set */
    return fullCode[bitIndex] === '1' || fullCode[PRIVILEGE_INDEX.ALL] === '1';
  }

  /* This is the umbrella method for checking privileges on different features
     @name String
     @opts Object

     @opts should have either categoryId or subCategoryId field set.
     */
  hasPrivilege(name, opts) {
    /* eslint-disable */
    if (!has(PRIVILEGE_INDEX, name)) {
      throw new Error(`Privilige ${name} is not supported!`);
    }
    opts.module = opts.module || 'ANALYZE';

    const token = this.getTokenObj();
    const module =
      find(
        get(token, 'ticket.products.[0].productModules'),
        module => module.productModName === opts.module
      ) || [];

    const code = this.getCode(opts, module);

    /* prettier-ignore */
    switch (name) {
    case 'ACCESS':
      return this._isSet(code, PRIVILEGE_INDEX.ACCESS);
    case 'CREATE':
      return this._isSet(code, PRIVILEGE_INDEX.CREATE);
    case 'EXECUTE':
      return this._isSet(code, PRIVILEGE_INDEX.EXECUTE);
    case 'PUBLISH':
      return this._isSet(code, PRIVILEGE_INDEX.PUBLISH);
    case 'FORK':
      return this._isSet(code, PRIVILEGE_INDEX.FORK);
    case 'EDIT':
      return (
        this._isSet(code, PRIVILEGE_INDEX.EDIT) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token))
      );
    case 'EXPORT':
      return this._isSet(code, PRIVILEGE_INDEX.EXPORT);
    case 'DELETE':
      return (
        this._isSet(code, PRIVILEGE_INDEX.DELETE) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token))
      );
    default:
      return false;
    }
    /* eslint-enable */
  }

  getCode(opts, module) {
    if (opts.subCategoryId) {
      const subCategories = flatMap(
        module.prodModFeature,
        feature => feature.productModuleSubFeatures
      );
      const subCategory =
        find(
          subCategories,
          subFeature =>
            subFeature.prodModFeatureID.toString() ===
            opts.subCategoryId.toString()
        ) || {};

      return subCategory.privilegeCode || 0;
    }

    if (opts.categoryId) {
      const category =
        find(
          module.prodModFeature,
          feature =>
            feature.prodModFeatureID.toString() === opts.categoryId.toString()
        ) || {};

      return category.privilegeCode || 0;
    }
    // No privilege
    return 0;
  }
}
