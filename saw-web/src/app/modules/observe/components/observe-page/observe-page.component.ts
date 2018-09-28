import { Component, OnInit } from '@angular/core';

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';

const style = require('./observe-page.component.scss');

@Component({
  selector: 'observe-page',
  templateUrl: './observe-page.component.html',
  styles: [
    `:host {
      width: 100%;
    }`,
    style
  ]
})
export class ObservePageComponent implements OnInit {
  constructor(
    private menu: MenuService,
    private observe: ObserveService
  ) {}

  ngOnInit() {
    /* Needed to get the analyze service working correctly */
    this.menu.getMenu('ANALYZE');

    this.observe.reloadMenu().subscribe(
      menu => {
        this.observe.updateSidebar(menu);
        this.observe.redirectToFirstDash(menu);
      }
    );
  }
}
