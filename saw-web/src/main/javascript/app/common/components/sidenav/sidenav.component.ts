import { Component, Input, Inject, Injectable } from '@angular/core';
import * as get from 'lodash/get';
import { ComponentHandler } from './../../utils/componentHandler';
import { MenuService } from './../../services/menu.service';

const template = require('./sidenav.component.html');
require('./sidenav.component.scss');

@Component({
  selector: 'sidenav',
  template
})

@Injectable()
export class SidenavComponent { 

  @Input() menu: any;
  @Input() id: any;

  constructor(@Inject('$componentHandler') private chp: ComponentHandler, private menuservice: MenuService) { }

  @Input() unregister = this.unregister = this.chp.register(this.id, this);
  
  ngOnInit() {
  	this.unregister = this.chp.register(this.id, this);
    this._moduleName = '';
  }

  getMenuHeader() {
    return {
      analyze: 'Analyses',
      observe: 'Dashboards',
      admin: 'Manage',
      workbench: 'WORKBENCH'
    }[this._moduleName.toLowerCase()] || '';
  }

  update(data, moduleName = '') {
    this._moduleName = moduleName;
    this.menu = data;
  }
}

