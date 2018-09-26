import { Injectable } from '@angular/core';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as startsWith from 'lodash/startsWith';
import { JwtService } from './jwt.service';
import { SidenavMenuService } from '../components/sidenav/sidenav-menu.service';

export const SAW_MODULES = {
  OBSERVE: { name: 'OBSERVE', codePrefix: 'OBSR' },
  ANALYZE: { name: 'ANALYZE', codePrefix: 'ANLYS' },
  WORKBENCH: { name: 'WORKBENCH', codePrefix: 'WRK' }
};

@Injectable()
export class MenuService {
  _menuCache: Object = {};

  constructor(
    private _jwtService: JwtService,
    private _sidenavMenuService: SidenavMenuService
  ) {}

  updateMenu(data, moduleName) {
    this._sidenavMenuService.updateMenu(data, moduleName);
  }

  getMenu(moduleName) {
    const token = this._jwtService.getTokenObj();

    const cachedMenu = this._menuCache[moduleName];

    if (cachedMenu) {
      return cachedMenu;
    }

    const menuPromise = new Promise((resolve, reject) => {
      const error = (desc = 'Error occurred while getting menu.') => {
        reject(desc);
        return;
      };

      if (!token) {
        return error('Auth token not found');
      }

      moduleName = moduleName.toUpperCase();

      const product = get(token, 'ticket.products.[0]');
      const targetModule = find(
        product.productModules,
        module => module.productModName === moduleName
      );

      if (!targetModule) {
        return error('Module name not found');
      }

      const features = filter(targetModule.prodModFeature, category =>
        startsWith(category.prodModCode, SAW_MODULES[moduleName].codePrefix)
      );

      resolve(
        map(features, feature => {
          const obj: any = {
            id: feature.prodModFeatureID,
            name: feature.prodModFeatureName || feature.prodModFeatureDesc,
            data: feature
          };

          /* Since there are no subcategories in observe, don't add them if they're there */
          obj.children = map(feature.productModuleSubFeatures, subfeature => {
            // Workbench uses defaultURL attribute value to navigate from side nav.
            // 'WRK000001' is the module code for workbench.
            const url =
              subfeature.prodModCode === 'WRK000001'
                ? [`/${moduleName.toLowerCase()}`, `${subfeature.defaultURL}`]
                : [
                  `/${moduleName.toLowerCase()}`,
                  `${subfeature.prodModFeatureID}`
                ];
            return {
              id: subfeature.prodModFeatureID,
              name:
                subfeature.prodModFeatureName || subfeature.prodModFeatureDesc,
              url,
              data: subfeature
            };
          });
          return obj;
        })
      );
    });

    this._menuCache[moduleName] = menuPromise;
    return menuPromise;
  }
}
