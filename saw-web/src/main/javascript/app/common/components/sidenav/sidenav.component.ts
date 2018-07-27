import { Component, Input, Inject, ViewChild } from '@angular/core';

import * as get from 'lodash/get';
import { ComponentHandler } from './../../utils/componentHandler';

const template = require('./sidenav.component.html');
require('./sidenav.component.scss');

@Component({
  selector: 'sidenav',
  template
})

export class SidenavComponent { 

  @Input() menu: any;
  @Input() id: any;

  constructor(@Inject('$componentHandler') private chp: ComponentHandler) { }
  @ViewChild('sidenav') public sidenav;

  public unregister: any;
  public _moduleName: string;
  
  ngOnInit() {
  	this.unregister = this.chp.register(this.id, this);
    this._moduleName = '';
  }

  getMenuHeader() {
    return {
      analyze: 'Analysis',
      observe: 'Dashboards',
      admin: 'Manage',
      workbench: 'WORKBENCH'
    }[this._moduleName.toLowerCase()] || '';
  }

  update(data, moduleName = '') {
    this._moduleName = moduleName;
    this.menu = data;
  }

  toggleNav() {
    this.sidenav.toggle();
  }
}

