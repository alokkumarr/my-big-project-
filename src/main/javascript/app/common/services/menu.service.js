import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as find from 'lodash/find';

export function MenuService($q, JwtService) {
  'ngInject';

  return {
    getMenu
  };

  function getMenu(moduleName) {
    const token = JwtService.getTokenObj();
    const deferred = $q.defer();

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

    deferred.resolve(map(module.prodModFeature, feature => {
      const obj = {
        id: feature.prodModFeatureID,
        name: feature.prodModFeatureName || feature.prodModFeatureDesc,
        data: feature
      };

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
