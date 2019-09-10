import { Component, OnInit } from '@angular/core';

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { Store } from '@ngxs/store';
import { CommonLoadAllMetrics } from 'src/app/common/actions/common.actions';

@Component({
  selector: 'observe-page',
  templateUrl: './observe-page.component.html',
  styleUrls: ['./observe-page.component.scss']
})
export class ObservePageComponent implements OnInit {
  constructor(
    private menu: MenuService,
    public observe: ObserveService,
    private store: Store
  ) {}

  ngOnInit() {
    /* Needed to get the analyze service working correctly */
    this.menu.getMenu('ANALYZE');

    this.observe.reloadMenu().subscribe(menu => {
      this.observe.updateSidebar(menu);
    });

    this.store.dispatch(new CommonLoadAllMetrics());
  }
}
