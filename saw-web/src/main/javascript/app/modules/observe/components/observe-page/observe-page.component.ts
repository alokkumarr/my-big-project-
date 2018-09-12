import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';

const template = require('./observe-page.component.html');
require('./observe-page.component.scss');

import { Component } from '@angular/core';

@Component({
  selector: 'observe-page',
  template: template
})
export class ObservePageComponent {
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
