import * as get from 'lodash/get';
import * as has from 'lodash/has';
import * as isArray from 'lodash/isArray';
import * as padStart from 'lodash/padStart';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';
import * as lowerCase from 'lodash/lowerCase';
import * as toUpper from 'lodash/toUpper';
import AppConfig from '../../../../appConfig';
import { Injectable } from '@angular/core';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpFilter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import {
  USER_ANALYSIS_CATEGORY_NAME,
  USER_ANALYSIS_SUBCATEGORY_NAME
} from '../consts';
import { HttpClient, HttpHeaders } from '@angular/common/http';

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
  constructor(public _http: HttpClient) {}

  private _localStorage = window.localStorage;
  private _refreshTokenKey = `${AppConfig.login.jwtKey}Refresh`;

  set(accessToken, refreshToken) {
    this._localStorage[AppConfig.login.jwtKey] = accessToken;
    this._localStorage[this._refreshTokenKey] = refreshToken;
  }

  get() {
    return this._localStorage[AppConfig.login.jwtKey];
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
    return this._localStorage[this._refreshTokenKey];
  }

  get refreshTokenObject() {
    const rToken = this.getRefreshToken();

    if (!rToken) {
      return null;
    }
    const parsedJwt = this.parseJWT(rToken);

    return parsedJwt;
  }

  validity() {
    return new Date(this.getTokenObj().ticket.validUpto);
  }

  /**
   * Returns the id first subcategory id in the auth token.
   *
   * @readonly
   * @type {number}
   * @memberof JwtService
   */
  get findDefaultCategoryId() {
    const token = this.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    const checkPermissionForSubCat = fpPipe(
      fpFlatMap(module => module.prodModFeature),
      fpFlatMap(subModule => subModule.productModuleSubFeatures),
      fpFilter(({ prodModFeatureID }) => {
        return parseInt(prodModFeatureID)
      })
    )(product.productModules);
    return checkPermissionForSubCat[0].prodModFeatureID;
  }

  /**
   * Returns the id of user's private sub category.
   *
   * @readonly
   * @type {number}
   * @memberof JwtService
   */
  get userAnalysisCategoryId(): number {
    const productModules =
      get(this.getTokenObj(), 'ticket.products.0.productModules') || [];
    const analyzeModule =
      find(productModules, module => module.productModName === 'ANALYZE') || {};
    const userCategory =
      find(
        analyzeModule.prodModFeature || [],
        category =>
          lowerCase(category.prodModFeatureName) ===
          lowerCase(USER_ANALYSIS_CATEGORY_NAME)
      ) || {};
    const userSubcategory =
      find(
        userCategory.productModuleSubFeatures || [],
        subCat =>
          lowerCase(subCat.prodModFeatureName) ===
          lowerCase(USER_ANALYSIS_SUBCATEGORY_NAME)
      ) || {};

    // If there's an issue with getting category's id, return 0. This is not ideal,
    // but better than analysis getting assigned to something like NaN category.
    // This OR condition is not expected to happen, ever. If it does, there's a bigger
    // problem somewhere else.
    return +userSubcategory.prodModFeatureID || 0;
  }

  destroy() {
    this._localStorage.removeItem(AppConfig.login.jwtKey);
    this._localStorage.removeItem(this._refreshTokenKey);
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

    return parsedJwt;
  }

  get customerCode() {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.custCode', 'Synchronoss');
  }

  get customerId(): string {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.custID', '');
  }

  get productId(): string {
    const token = this.getTokenObj();
    if (!token) {
      return '';
    }

    return get(token, 'ticket.defaultProdID', '');
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

    return this.isPrivilegeSet(name, code);
  }

  hasPrivilegeForDraftsFolder(privilege) {
    const token = this.getTokenObj();
    const modules = get(token, 'ticket.products[0].productModules');
    const analyzeModule = find(
      modules,
      ({ productModName }) => productModName === 'ANALYZE'
    );
    const features = analyzeModule ? analyzeModule.prodModFeature : [];
    const myAnallysisFolder = find(
      features,
      ({ prodModFeatureName }) => prodModFeatureName === 'My Analysis'
    );
    const subFeatures = myAnallysisFolder
      ? myAnallysisFolder.productModuleSubFeatures
      : [];
    const draftsFolder = find(
      subFeatures,
      ({ prodModFeatureName }) => toUpper(prodModFeatureName) === 'DRAFTS'
    );
    if (draftsFolder) {
      const code = draftsFolder.privilegeCode;
      return this.isPrivilegeSet(privilege, code);
    }
    return false;
  }

  isPrivilegeSet(privilege, code) {
    /* prettier-ignore */
    switch (privilege) {
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
        return this._isSet(code, PRIVILEGE_INDEX.EDIT);
      case 'EXPORT':
        return this._isSet(code, PRIVILEGE_INDEX.EXPORT);
      case 'DELETE':
        return this._isSet(code, PRIVILEGE_INDEX.DELETE);
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

  validateToken() {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${this.get()}`
      })
    };

    return this._http
      .post(AppConfig.login.url + '/auth/validateToken', httpOptions)
      .toPromise();
  }

  fetchCategoryDetails(categoryId) {
    const token = this.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    return fpPipe(
      fpFlatMap(module => module.prodModFeature),
      fpFlatMap(subModule => subModule.productModuleSubFeatures),
      fpFilter(({ prodModFeatureID }) => {
        return parseInt(prodModFeatureID, 10) === parseInt(categoryId, 10);
      })
    )(product.productModules);
  }

  fetchChildren(children) {
    return children.filter(
      child => !child.data.systemCategory
    );
  }
}
