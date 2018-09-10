import { Injectable } from '@angular/core';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as startsWith from 'lodash/startsWith';

import { JwtService } from '../../../login/services/jwt.service';
import { ComponentHandler } from './componentHandler';

export const SAW_MODULES = {
  OBSERVE: {name: 'OBSERVE', codePrefix: 'OBSR'},
  ANALYZE: {name: 'ANALYZE', codePrefix: 'ANLYS'},
  WORKBENCH: {name: 'WORKBENCH', codePrefix: 'WRK'}
};

@Injectable()
export class MenuService {
  _menuCache: Object = {};

  constructor(
    private _jwtService: JwtService,
    private _$componentHandler: ComponentHandler
  ) {}

  updateMenu(data, moduleName, componentId = 'left-side-nav') {
    const menu = this._$componentHandler.get(componentId)[0];
    menu.update(data, moduleName);
    this._menuCache[moduleName] = data;
  }

  getMenu(moduleName) {
    const token = this._jwtService.getTokenObj();

    const cachedMenu = this._menuCache[moduleName];

    if (cachedMenu) {
      return cachedMenu;
    }

    const menuPromise =  new Promise((resolve, reject) => {
      const error = (desc = 'Error occurred while getting menu.') => {
        reject(desc);
        return;
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

      resolve(map(features, feature => {
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
    });

    this._menuCache[moduleName] = menuPromise;
    return menuPromise;
  }
}
