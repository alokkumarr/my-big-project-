import { Component, OnInit, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';
import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';

import * as find from 'lodash/find';

const template = require('./save-dashboard.component.html');
require('./save-dashboard.component.scss');

@Component({
  selector: 'save-dashboard',
  template
})
export class SaveDashboardComponent implements OnInit {

  private dashboard: any;
  public categories = [];

  constructor(private dialogRef: MdDialogRef<SaveDashboardComponent>,
    @Inject(MD_DIALOG_DATA) private data: any,
    private menu: MenuService,
    private observe: ObserveService
  ) { }

  ngOnInit() {
    this.dashboard = this.data.dashboard;
    this.menu.getMenu('OBSERVE').then(data => {
      this.categories = data;

      /* Find the first category that has a subcategory, and assign that subcategory
         to dashboard */
      const category = find(this.categories, category => category.children.length > 0);
      if (category) {
        this.dashboard.categoryId = this.dashboard.categoryId || category.children[0].id;
      }
    });
  }

  closeDashboard(data) {
    this.dialogRef.close(data);
  }

  isValid(dashboard) {
    return Boolean(dashboard.name);
  }

  saveDashboard() {
    if (!this.isValid(this.dashboard)) {
      return;
    }
    this.observe.saveDashboard(this.dashboard).subscribe(data => {
      this.closeDashboard(data.entityId);
    }, err => {
    });
  }
}
