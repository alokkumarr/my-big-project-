import { Injectable } from '@angular/core';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as includes from 'lodash/includes';
import { JwtService } from './jwt.service';
import { SidenavMenuService } from '../components/sidenav/sidenav-menu.service';

@Injectable()
export class MenuService {
  constructor(
    public _jwtService: JwtService,
    public _sidenavMenuService: SidenavMenuService
  ) {}

  updateMenu(data, moduleName) {
    this._sidenavMenuService.updateMenu(data, moduleName);
  }

  getMenu(moduleName) {
    const token = this._jwtService.getTokenObj();

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
        return error('Module name for menu not found!');
      }

      const features = filter(
        targetModule.prodModFeature,
        ({ prodModCode }) => prodModCode === targetModule.productModCode
      );

      const menu = map(features, feature => {
        const obj: any = {
          id: feature.prodModFeatureID,
          name: feature.prodModFeatureName || feature.prodModFeatureDesc,
          data: feature
        };

        /* Since there are no subcategories in observe, don't add them if they're there */
        obj.children = map(feature.productModuleSubFeatures, subfeature => {
          // Only Analyze and observe have dynamic categories/sub-categories.
          // Rest all modules uses and should use "defaultURL" field for routing
          const dynamicMenuModules = ['ANLYS00001', 'OBSR000001'];
          const url = includes(dynamicMenuModules, subfeature.prodModCode)
            ? [`/${moduleName.toLowerCase()}`, `${subfeature.prodModFeatureID}`]
            : [`/${moduleName.toLowerCase()}/${subfeature.defaultURL}`];

          return {
            id: subfeature.prodModFeatureID,
            name:
              subfeature.prodModFeatureName || subfeature.prodModFeatureDesc,
            url,
            data: subfeature
          };
        });
        return obj;
      });
      resolve(menu);
    });

    return menuPromise;
  }
}
