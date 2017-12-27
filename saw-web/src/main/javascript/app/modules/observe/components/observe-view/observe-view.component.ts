import { Component, OnInit, Inject } from '@angular/core';

import { MdDialog } from '@angular/material';
import { Transition } from '@uirouter/angular';
import { Dashboard } from '../../models/dashboard.interface';

import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
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
    public dialog: MdDialog,
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

  createDashboard() {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog'
    });
  }

  loadDashboard() {
    this.headerProgress.show();
    this.observe.getDashboard(this.dashboardId).subscribe(data => {
      this.dashboard = data;
      this.headerProgress.hide();
    })
  }
}
