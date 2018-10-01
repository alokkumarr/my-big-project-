import { Component, OnInit } from '@angular/core';

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';

@Component({
  selector: 'observe-page',
  templateUrl: './observe-page.component.html',
  styles: [
    `:host {
      width: 100%;
    }`
  ]
})
export class ObservePageComponent implements OnInit {
  constructor(private menu: MenuService, private observe: ObserveService) {}

  ngOnInit() {
    /* Needed to get the analyze service working correctly */
    this.menu.getMenu('ANALYZE');

    this.observe.reloadMenu().subscribe(menu => {
      this.observe.updateSidebar(menu);
      this.observe.redirectToFirstDash(menu);
    });
  }
}
