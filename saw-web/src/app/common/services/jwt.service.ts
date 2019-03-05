import * as get from 'lodash/get';
import * as has from 'lodash/has';
import * as isArray from 'lodash/isArray';
import * as padStart from 'lodash/padStart';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import AppConfig from '../../../../appConfig';
import { Injectable } from '@angular/core';
import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../consts';

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
    return isArray(customConfig) ? customConfig.includes(configName) : false;
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

  /**
   * Returns the id of user's private sub category.
   *
   * @readonly
   * @type {(number | string)}
   * @memberof JwtService
   */
  get userAnalysisCategoryId(): number | string {
    const productModules =
      get(this.getTokenObj(), 'ticket.products.0.productModules') || [];
    const analyzeModule =
      find(productModules, module => module.productModName === 'ANALYZE') || {};
    const userCategory =
      find(
        analyzeModule.prodModFeature || [],
        category => category.prodModFeatureName === USER_ANALYSIS_CATEGORY_NAME
      ) || {};
    const userSubcategory =
      find(
        userCategory.productModuleSubFeatures || [],
        subCat => subCat.prodModFeatureName === USER_ANALYSIS_SUBCATEGORY_NAME
      ) || {};

    return userSubcategory.prodModFeatureID;
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
    const parsedJwt = this.parseJWT(this.get());

    // TODO remove hardcoded insightsModule when the ticket is successfully tested
    // tslint:disable
    // const insightsModule = {
    //   "prodCode":"SAWD0000012131",
    //   "productModName":"INSIGHTS",
    //   "productModDesc":"Insights Module",
    //   "productModCode":"INSIGH00001",
    //   "productModID":"1324244",
    //   "moduleURL":"http://localhost:4200/assets/insights.umd.js",
    //   "defaultMod":"1",
    //   "privilegeCode":128,
    //   "prodModFeature": [{
    //     prodModFeatureName: 'SubModules',
    //     prodModCode: 'INSIGH00001',
    //     productModuleSubFeatures: [{
    //     //   prodModFeatureName: 'IOT',
    //     //   prodModFeatureDesc: 'Iot',
    //     //   defaultURL: 'iot',
    //     //   prodModFeatureID: 'iot',
    //     //   prodModFeatrCode: 'iot',
    //     //   prodModCode: 'INSIGH00001',
    //     //   roleId: 1
    //     // }, {
    //       prodModFeatureName: 'REVIEW',
    //       prodModFeatureDesc: 'Review',
    //       defaultURL: 'review',
    //       prodModFeatureID: 'review',
    //       prodModFeatrCode: 'review',
    //       roleId: 1
    //     }, {
    //       prodModFeatureName: 'DETECTOR',
    //       prodModFeatureDesc: 'Detector',
    //       defaultURL: 'detector',
    //       prodModFeatureID: 'detector',
    //       prodModFeatrCode: 'detector',
    //       roleId: 1
    //     }, {
    //       prodModFeatureName: 'FORECAST',
    //       prodModFeatureDesc: 'Forecast',
    //       defaultURL: 'forecast',
    //       prodModFeatureID: 'forecast',
    //       prodModFeatrCode: 'forecast',
    //       roleId: 1
    //     }, {
    //       prodModFeatureName: 'REALTIME',
    //       prodModFeatureDesc: 'Realtime',
    //       defaultURL: 'realtime',
    //       prodModFeatureID: 'realtime',
    //       prodModFeatrCode: 'realtime',
    //       roleId: 1
    //     }, {
    //       prodModFeatureName: 'CORRELATOR',
    //       prodModFeatureDesc: 'Correlator',
    //       defaultURL: 'correlator',
    //       prodModFeatureID: 'correlator',
    //       prodModFeatrCode: 'correlator',
    //       roleId: 1
    //     }, {
    //       prodModFeatureName: 'UNBILLED_USAGE',
    //       prodModFeatureDesc: 'UnbilledUsage',
    //       defaultURL: 'unbilledUsage',
    //       prodModFeatureID: 'unbilledUsage',
    //       prodModFeatrCode: 'unbilledUsage',
    //       roleId: 1
    //     }]
    //   }]
    // }
    // // tslint:enable
    // parsedJwt.ticket.products[0].productModules.push(insightsModule);
    return parsedJwt;
  }

  get customerCode() {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.custCode', 'Synchronoss');
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
            customerCode: get(token, 'ticket.custCode', 'SNCR')
            // dataSecurityKey: get(token, 'ticket.dataSecurityKey')
          }
        ]
      }
    };
  }

  getValidityReason(token) {
    if (!token) {
      token = this.getTokenObj();
    }
    return token.ticket.validityReason;
  }

  getLoginId() {
    return get(this.getTokenObj(), 'ticket.masterLoginId').toString();
  }

  getUserId() {
    return get(this.getTokenObj(), 'ticket.userId').toString();
  }

  getUserName() {
    return get(this.getTokenObj(), 'ticket.userFullName').toString();
  }

  getProductName() {
    return get(this.getTokenObj(), 'ticket.products[0].productCode').toString();
  }

  _isRole(token, role) {
    const roleType = get(token, 'ticket.roleType');
    return roleType === role;
  }

  isAdmin() {
    const token = this.getTokenObj();
    return this._isRole(token, 'ADMIN');
  }

  isOwner(token, creatorId) {
    creatorId = creatorId || '';
    return creatorId.toString() === get(token, 'ticket.userId').toString();
  }

  _isSet(code, bitIndex) {
    const fullCode = padStart(
      // tslint:disable-next-line
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
    const targetModule =
      find(
        get(token, 'ticket.products.[0].productModules'),
        module => module.productModName === opts.module
      ) || [];

    const code = this.getCode(opts, targetModule);

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
    case 'SCHEDULE':
      return this._isSet(code, PRIVILEGE_INDEX.PUBLISH);
    case 'FORK':
      return this._isSet(code, PRIVILEGE_INDEX.FORK);
    case 'EDIT':
      return (
        this._isSet(code, PRIVILEGE_INDEX.EDIT) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin())
      );
    case 'EXPORT':
      return this._isSet(code, PRIVILEGE_INDEX.EXPORT);
    case 'DELETE':
      return (
        this._isSet(code, PRIVILEGE_INDEX.DELETE) ||
        (this.isOwner(token, opts.creatorId) || this.isAdmin())
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
