import { Component, OnInit, Inject } from '@angular/core';

import { Transition } from '@uirouter/angular';
import { Dashboard } from '../../models/dashboard.interface';

import { ObserveService } from '../../services/observe.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';

const template = require('./observe-view.component.html');

@Component({
  selector: 'observe-view',
  template
})

export class ObserveViewComponent implements OnInit {
  private dashboardId: string;
  private dashboard: Dashboard;

  constructor(
    private observe: ObserveService,
    private headerProgress: HeaderProgressService,
    private transition: Transition
  ) {
    this.dashboardId = this.transition.params().dashboardId;
  }

  ngOnInit() {
    if (this.dashboardId) {
      this.loadDashboard();
    }
  }

  loadDashboard() {
    this.headerProgress.show();
    this.observe.getDashboard(this.dashboardId).subscribe(data => {
      this.dashboard = data;
      this.headerProgress.hide();
    })
  }
}
