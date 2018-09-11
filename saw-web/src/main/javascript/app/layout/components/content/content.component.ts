import { Component } from '@angular/core';
import { Router } from '@angular/router';
import * as get from 'lodash/get';
import * as find from 'lodash/find';

import { JwtService } from '../../../../login/services/jwt.service';

const template = require('./content.component.html');

@Component({
  selector: 'layout-content',
  template
})
export class LayoutContentComponent {

  constructor(
    private _jwt: JwtService,
    private _router: Router
  ) {}

  ngOnInit() {
    this.goToAnalyzePage();
  }

  goToAnalyzePage() {
    const analyzeModuleName = 'ANALYZE';
    const modules = get(this._jwt.getTokenObj(), 'ticket.products[0].productModules');
    const analyzeModuleExists = find(modules, ({productModName}) => productModName === analyzeModuleName);

    if (analyzeModuleExists) {
      this._router.navigate(['analyze']);
    }
  }
}
