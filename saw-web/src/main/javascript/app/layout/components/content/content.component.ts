import { Component } from '@angular/core';
import { Router, NavigationEnd, NavigationStart } from '@angular/router';
import * as fpFirst from 'lodash/fp/first';
import * as fpGet from 'lodash/fp/get';
import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as split from 'lodash/split';
import * as fpPipe from 'lodash/fp/pipe';

import { MenuService } from '../../../common/services/menu.service';
import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';
import { JwtService } from '../../../../login/services/jwt.service';

const template = require('./content.component.html');

@Component({
  selector: 'layout-content',
  template
})
export class LayoutContentComponent {

  constructor(
    private _menu: MenuService,
    private _jwt: JwtService,
    private _router: Router
  ) {
    this._router.events
      .subscribe(event => {
        if (event instanceof NavigationStart) {
          console.log(event);
        }
        this.saveAnalyzeCategoryId(event);
      });
  }

  ngOnInit() {
    this.goToAnalyzePage();
  }

  goToAnalyzePage() {
    const analyzeModuleName = 'ANALYZE';
    const modules = get(this._jwt.getTokenObj(), 'ticket.products[0].productModules');
    const analyzeModuleExists = find(modules, ({productModName}) => productModName === analyzeModuleName);

    if (analyzeModuleExists) {
      this._menu.getMenu(analyzeModuleName).then(menu => {
        this.goToDefaultChildStateIfNeeded(menu);
      });
    }
  }

  goToDefaultChildStateIfNeeded(menu) {
    const id = window.localStorage[LAST_ANALYSES_CATEGORY_ID] ||
    fpPipe(
      fpFirst,
      fpGet('children'),
      fpFirst,
      fpGet('id')
    )(menu);
    this._router.navigate(['analyze', id]);
  }

  saveAnalyzeCategoryId(event) {
    if (event instanceof NavigationEnd) {
      const [base, id] = split(event.url, '/');
      if (base === 'analyze' && id) {
        const key = `${LAST_ANALYSES_CATEGORY_ID}-${this._jwt.getUserId()}`;
        window.localStorage[key] = id;
      }
    }
  }
}
