import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import * as split from 'lodash/split';

import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';
import { JwtService } from '../../../common/services';
const template = require('./analyze-page.component.html');

@Component({
  selector: 'analyze-page',
  template
})

export class AnalyzePageComponent {
  constructor(
    private _jwt: JwtService,
    private _router: Router
  ) {
    this._router.events
      .subscribe(event => {
        this.saveAnalyzeCategoryId(event);
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


}
