import { Component, OnInit, Inject } from '@angular/core';

import { MdDialog } from '@angular/material';
import { Transition } from '@uirouter/angular';
import { Dashboard } from '../../models/dashboard.interface';

import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
import { ObserveService } from '../../services/observe.service';
import { JwtService } from '../../../../../login/services/jwt.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';

const template = require('./observe-view.component.html');

@Component({
  selector: 'observe-view',
  template
})

export class ObserveViewComponent implements OnInit {
  private dashboardId: string;
  private subCategoryId: string;
  private dashboard: Dashboard;
  private privileges = {
    create: false,
    delete: false,
    edit: false
  };

  constructor(
    public dialog: MdDialog,
    private observe: ObserveService,
    private headerProgress: HeaderProgressService,
    private jwt: JwtService,
    private transition: Transition
  ) {
    this.dashboardId = this.transition.params().dashboard;
    this.subCategoryId = this.transition.params().subCategory;

    this.loadPrivileges();
  }

  ngOnInit() {
    if (this.dashboardId) {
      this.loadDashboard();
    }
  }

  createDashboard(): void {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog'
    });
  }

  loadDashboard(): void {
    this.headerProgress.show();
    this.observe.getDashboard(this.dashboardId).subscribe(data => {
      this.dashboard = data;
      this.headerProgress.hide();
    })
  }

  loadPrivileges(): void {
    if (!this.subCategoryId) {
      return;
    }

    const moduleName = 'OBSERVE';

    this.privileges.create = this.jwt.hasPrivilege('CREATE', {
      module: moduleName,
      subCategoryId: this.subCategoryId
    });

    if (!this.dashboard) {
      return;
    }

    this.privileges.edit = this.jwt.hasPrivilege('EDIT', {
      module: moduleName,
      subCategoryId: this.subCategoryId,
      creatorId: this.dashboard.createdBy
    });

    this.privileges.delete = this.jwt.hasPrivilege('DELETE', {
      module: moduleName,
      subCategoryId: this.subCategoryId,
      creatorId: this.dashboard.createdBy
    });
  }
}
