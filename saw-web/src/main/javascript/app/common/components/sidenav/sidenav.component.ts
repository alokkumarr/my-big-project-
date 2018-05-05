import {UpgradeComponent} from '@angular/upgrade/static';
import { Component, Input, Inject, Injectable } from '@angular/core';
import * as get from 'lodash/get';
import { ComponentHandler } from './../../utils/componentHandler';
import { MenuService } from './../../services/menu.service';
import {AccordionMenu} from '../accordionMenu';

const template = require('./sidenav.component.html');
require('./sidenav.component.scss');

@Component({
  selector: 'sidenav',
  template
})

@Injectable()
export class SidenavComponent { 

  @Input() menu: any;

  constructor(@Inject('$componentHandler') private chp: ComponentHandler, private menuservice: MenuService) { }
  
  ngOnInit() {
    this._moduleName = 'ANALYZE';
    this.unregister = this.chp.register('left-side-nav', this);
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

  getMenuDetails() {
    this.menuservice.getMenu('ANALYZE').then(data => {
  	  this.menu = data;
    });
    return this.menu;
  }
}

