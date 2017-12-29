import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as startsWith from 'lodash/startsWith';

export const SAW_MODULES = {
  OBSERVE: {name: 'OBSERVE', codePrefix: 'OBSR'},
  ANALYZE: {name: 'ANALYZE', codePrefix: 'ANLYS'}
};

export class MenuService {
  constructor($q, JwtService, $componentHandler) {
    'ngInject';
    this._JwtService = JwtService;
    this._$q = $q;
    this._$componentHandler = $componentHandler;
    this._menuCache = {};
  }

  updateMenu(data, moduleName, componentId = 'left-side-nav') {
    const menu = this._$componentHandler.get(componentId)[0];
    menu.update(data, moduleName);
    this._menuCache[moduleName] = data;
  }

  getCachedMenu(moduleName) {
    return this._menuCache[moduleName];
  }

  getMenu(moduleName) {
    const token = this._JwtService.getTokenObj();
    const deferred = this._$q.defer();

    const error = (desc = 'Error occurred while getting menu.') => {
      deferred.reject(desc);
      return deferred.promise;
    };

    if (!token) {
      return error('Auth token not found');
    }

    moduleName = moduleName.toUpperCase();

    const product = get(token, 'ticket.products.[0]');
    const module = find(product.productModules, module => module.productModName === moduleName);

    if (!module) {
      return error('Module name not found');
    }

    const features = filter(module.prodModFeature, category => startsWith(category.prodModCode, SAW_MODULES[moduleName].codePrefix));

    deferred.resolve(map(features, feature => {
      const obj = {
        id: feature.prodModFeatureID,
        name: feature.prodModFeatureName || feature.prodModFeatureDesc,
        data: feature
      };

      /* Since there are no subcategories in observe, don't add them if they're there */
      obj.children = map(feature.productModuleSubFeatures, subfeature => {
        return {
          id: subfeature.prodModFeatureID,
          name: subfeature.prodModFeatureName || subfeature.prodModFeatureDesc,
          url: `#!/${moduleName.toLowerCase()}/${subfeature.prodModFeatureID}`,
          data: subfeature
        };
      });
      return obj;
    }));

    return deferred.promise;
  }
}
