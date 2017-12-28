import { Component, OnInit, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';
import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { JwtService } from '../../../../../login/services/jwt.service';

import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';

const template = require('./save-dashboard.component.html');
require('./save-dashboard.component.scss');

@Component({
  selector: 'save-dashboard',
  template
})
export class SaveDashboardComponent implements OnInit {

  private dashboard: any;
  public categories = [];
  public showProgress = false;

  constructor(private dialogRef: MdDialogRef<SaveDashboardComponent>,
    @Inject(MD_DIALOG_DATA) private data: any,
    private menu: MenuService,
    private observe: ObserveService,
    private jwt: JwtService
  ) { }

  ngOnInit() {
    this.dashboard = this.data.dashboard;

    /* Load categories and filter out non-privileged sub categories */
    this.menu.getMenu('OBSERVE').then(data => {
      const categories = map(data, clone);
      forEach(categories, category => {
        category.children = this.filterPrivilegedSubCategories(category.children);
      });

      this.categories = categories;

      /* Find the first category that has a subcategory, and assign that subcategory
         to dashboard */
      const category = find(this.categories, category => category.children.length > 0);
      if (category) {
        this.dashboard.categoryId = this.dashboard.categoryId || category.children[0].id.toString();
      }
    });
  }

  filterPrivilegedSubCategories(subCategories) {
    return filter(subCategories, subCategory => this.hasPrivilege(subCategory.id));
  }

  hasPrivilege(subCategoryId) {
    const moduleName = 'OBSERVE';
    return this.jwt.hasPrivilege('CREATE', {
      module: moduleName,
      subCategoryId
    });
  }

  closeDashboard(data) {
    this.dialogRef.close(data);
  }

  isValid(dashboard) {
    return Boolean(dashboard.name) && Boolean(dashboard.categoryId);
  }

  saveDashboard() {
    if (!this.isValid(this.dashboard)) {
      return;
    }
    this.showProgress = true;
    this.observe.saveDashboard(this.dashboard).subscribe(data => {
      this.showProgress = false;
      this.closeDashboard(data);
    }, err => {
      this.showProgress = false;
    });
  }
}
