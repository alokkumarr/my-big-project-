import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import * as split from 'lodash/split';
import * as fpFirst from 'lodash/fp/first';
import * as fpGet from 'lodash/fp/get';
import * as fpPipe from 'lodash/fp/pipe';
import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';
import { MenuService } from '../../../common/services/menu.service';
import { JwtService } from '../../../../login/services/jwt.service';
const template = require('./analyze-page.component.html');

@Component({
  selector: 'analyze-page',
  template
})

export class AnalyzePageComponent implements OnInit {
  constructor(
    private _menu: MenuService,
    private _jwt: JwtService,
    private _router: Router
  ) {
    this._router.events
      .subscribe(event => {
        this.saveAnalyzeCategoryId(event);
      });
  }

  ngOnInit() {
    this._menu.getMenu('ANALYZE').then(menu => {
      this.goToDefaultChildStateIfNeeded(menu);
    });
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
}
