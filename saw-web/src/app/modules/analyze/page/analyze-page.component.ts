import { Component } from '@angular/core';
import { Store } from '@ngxs/store';
import { Router, NavigationEnd } from '@angular/router';
import * as split from 'lodash/split';

import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';
import { JwtService } from '../../../common/services';
import { CommonLoadAllMetrics } from 'src/app/common/actions/menu.actions';

@Component({
  selector: 'analyze-page',
  templateUrl: 'analyze-page.component.html'
})
export class AnalyzePageComponent {
  constructor(
    private _jwt: JwtService,
    public _router: Router,
    private store: Store
  ) {
    this._router.events.subscribe(event => {
      this.saveAnalyzeCategoryId(event);
    });
    this.store.dispatch(new CommonLoadAllMetrics());
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
