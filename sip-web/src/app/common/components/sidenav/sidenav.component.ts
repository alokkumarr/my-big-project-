import { Component, Input, ViewChild, OnInit } from '@angular/core';

import { SidenavMenuService } from './sidenav-menu.service';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent implements OnInit {
  @Input() menu: any;
  @Input() id: any;

  constructor(public _sidenav: SidenavMenuService) {}
  @ViewChild('sidenav', { static: true }) public sidenav;

  public unregister: any;
  public _moduleName: string;

  ngOnInit() {
    this._sidenav.subscribe(({ menu, module }) => this.update(menu, module));
  }

  update(data, moduleName = '') {
    this._moduleName = moduleName;
    this.menu = data;
  }

  toggleNav() {
    // wait for menu to load its items
    setTimeout(() => {
      this.sidenav.toggle();
    }, 100);
  }
}
