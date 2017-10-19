import * as get from 'lodash/get';
import * as padStart from 'lodash/padStart';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';

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

export class JwtService {
  constructor($window, AppConfig) {
    'ngInject';

    this._$window = $window;
    this._AppConfig = AppConfig;

    this._refreshTokenKey = `${AppConfig.login.jwtKey}Refresh`;
  }

  set(accessToken, refreshToken) {
    this._$window.localStorage[this._AppConfig.login.jwtKey] = accessToken;
    this._$window.localStorage[this._refreshTokenKey] = refreshToken;
  }

  get() {
    return this._$window.localStorage[this._AppConfig.login.jwtKey];
  }

  getCategories () {
    const token = this.getTokenObj();
    const analyzeModule = find(
      get(token, 'ticket.products[0].productModules'),
      mod => mod.productModName === 'ANALYZE'
    );

    return get(analyzeModule, 'prodModFeature', []) || [];
  }

  getAccessToken() {
    return this.get();
  }

  getRefreshToken() {
    return this._$window.localStorage[this._refreshTokenKey];
  }

  validity() {
    return new Date(this.getTokenObj().ticket.validUpto);
  }

  destroy() {
    this._$window.localStorage.removeItem(this._AppConfig.login.jwtKey);
    this._$window.localStorage.removeItem(this._refreshTokenKey);
  }

  parseJWT(jwt) {
    const base64Url = jwt.split('.')[1];
    const base64 = base64Url.replace('-', '+').replace('_', '/');
    return angular.fromJson(this._$window.atob(base64));
  }

  /* Returs the parsed json object from the jwt token */
  getTokenObj() {
    const token = this.get();

    if (!token) {
      return null;
    }

    return this.parseJWT(this.get());
  }

  isValid(token) {
    return get(token, 'ticket.valid', false) &&
      get(token, 'ticket.validUpto', 0) >= Date.now();
  }

  /* Bootstraps request structure with necessary auth data */
  getRequestParams() {
    const token = this.getTokenObj();
    return {
      contents: {
        keys: [{
          customerCode: get(token, 'ticket.custCode', 'ATT')
          // dataSecurityKey: get(token, 'ticket.dataSecurityKey')
        }]
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
    const fullCode = padStart((code >>> 0).toString(2), PRIVILEGE_CODE_LENGTH, '0');
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
    if (!PRIVILEGE_INDEX[name]) {
      throw new Error(`Privilige ${name} is not supported!`);
    }
    opts.module = opts.module || 'ANALYZE';

    const token = this.getTokenObj();
    const module = find(
      get(token, 'ticket.products.[0].productModules'),
      module => module.productModName === opts.module
    ) || [];

    let code = 0; // No privilege

    if (opts.categoryId) {
      const category = find(module.prodModFeature, feature => feature.prodModFeatureID.toString() === opts.categoryId.toString()) || {};
      code = category.privilegeCode || 0;
    }

    if (opts.subCategoryId) {
      const subCategories = flatMap(module.prodModFeature, feature => feature.productModuleSubFeatures);
      const subCategory = find(subCategories, subFeature => subFeature.prodModFeatureID.toString() === opts.subCategoryId.toString()) || {};
      code = subCategory.privilegeCode || 0;
    }

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
      return this._isSet(code, PRIVILEGE_INDEX.EDIT) &&
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token));
    case 'EXPORT':
      return this._isSet(code, PRIVILEGE_INDEX.EXPORT);
    case 'DELETE':
      return this._isSet(code, PRIVILEGE_INDEX.DELETE) &&
        (this.isOwner(token, opts.creatorId) || this.isAdmin(token));
    default:
      return false;
    }
  }
}

export function JwtServiceFactory($window, AppConfig) {
  'ngInject';
  return new JwtService($window, AppConfig);
}
