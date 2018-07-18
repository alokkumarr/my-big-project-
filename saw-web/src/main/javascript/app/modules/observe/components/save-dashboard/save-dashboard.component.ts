import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { JwtService } from '../../../../../login/services/jwt.service';

import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as forEach from 'lodash/forEach';
import * as assign from 'lodash/assign';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';

const template = require('./save-dashboard.component.html');
require('./save-dashboard.component.scss');

@Component({
  selector: 'save-dashboard',
  template
})
export class SaveDashboardComponent implements OnInit {
  private dashboardForm: FormGroup;
  private dashboard: any;
  public categories = [];
  public showProgress = false;

  constructor(
    private dialogRef: MatDialogRef<SaveDashboardComponent>,
    @Inject(MAT_DIALOG_DATA) private data: any,
    private fb: FormBuilder,
    private menu: MenuService,
    private observe: ObserveService,
    private jwt: JwtService
  ) {
    this.createForm();
  }

  createForm() {
    this.dashboardForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      categoryId: ['', Validators.required]
    });
  }

  async ngOnInit() {
    this.dashboard = this.data.dashboard;

    this.dashboardForm.patchValue({
      name: this.dashboard.name,
      description: this.dashboard.description,
      categoryId: this.dashboard.categoryId
    });

    await this.loadCategories();

    this.setDefaultCategory();
  }

  async loadCategories() {
    const data = await this.menu.getMenu('OBSERVE');
    const categories = map(data, clone);
    forEach(categories, category => {
      category.children = this.filterPrivilegedSubCategories(category.children);
    });

    this.categories = categories;
    return this.categories;
  }

  /* Find the first category that has a subcategory, and assign that subcategory
     to dashboard */
  setDefaultCategory() {
    if (this.dashboard.categoryId) return;

    const category = find(
      this.categories,
      category => category.children.length > 0
    );
    if (category) {
      this.dashboard.categoryId = category.children[0].id.toString();
    }
  }

  filterPrivilegedSubCategories(subCategories) {
    return filter(subCategories, subCategory =>
      this.hasPrivilege(subCategory.id)
    );
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

  saveDashboard() {
    if (this.dashboardForm.invalid) {
      return;
    }

    this.showProgress = true;
    assign(this.dashboard, this.dashboardForm.value);
    this.observe.saveDashboard(this.dashboard).subscribe(
      data => {
        this.showProgress = false;
        this.closeDashboard(data);
      },
      err => {
        this.showProgress = false;
      }
    );
  }
}
