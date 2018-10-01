import { Component, OnInit, OnDestroy, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as forEach from 'lodash/forEach';
import * as assign from 'lodash/assign';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';

import { Dashboard } from '../../models/dashboard.interface';
import { ObserveService } from '../../services/observe.service';
import {
  MenuService,
  HeaderProgressService,
  JwtService
} from '../../../../common/services';
import { requireIf } from '../../validators/required-if.validator';

const style = require('./save-dashboard.component.scss');

export const REFRESH_INTERVALS = [
  {
    label: '1 minute',
    seconds: 60
  },
  {
    label: '2 minutes',
    seconds: 120
  },
  {
    label: '3 minutes',
    seconds: 180
  },
  {
    label: '4 minutes',
    seconds: 240
  },
  {
    label: '5 minutes',
    seconds: 300
  },
  {
    label: '6 minutes',
    seconds: 360
  },
  {
    label: '7 minutes',
    seconds: 420
  },
  {
    label: '8 minutes',
    seconds: 480
  },
  {
    label: '9 minutes',
    seconds: 540
  },
  {
    label: '10 minutes',
    seconds: 600
  }
];

@Component({
  selector: 'save-dashboard',
  templateUrl: './save-dashboard.component.html',
  styles: [
    `:host {
      display: block;
      padding: 10px;
      width: 400px;
    }`,
    style
  ]
})
export class SaveDashboardComponent implements OnInit, OnDestroy {
  public dashboardForm: FormGroup;
  public dashboard: Dashboard;
  public categories = [];
  public refreshIntervals = REFRESH_INTERVALS;
  public showProgress = false;
  public listeners: Array<Subscription> = [];
  progressSub;

  constructor(
    public dialogRef: MatDialogRef<SaveDashboardComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public fb: FormBuilder,
    public menu: MenuService,
    public observe: ObserveService,
    public jwt: JwtService,
    public _headerProgress: HeaderProgressService
  ) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
    this.createForm();
  }

  createForm() {
    this.dashboardForm = this.fb.group({
      name: ['', Validators.required],
      description: [''],
      categoryId: ['', Validators.required],
      autoRefreshEnabled: [false, Validators.required],
      refreshIntervalSeconds: [
        null,
        requireIf('autoRefreshEnabled', val => Boolean(val))
      ]
    });

    this.disableIntervalConditionally();
  }

  disableIntervalConditionally() {
    const sub = this.dashboardForm
      .get('autoRefreshEnabled')
      .valueChanges.subscribe(val => {
        const action = val ? 'enable' : 'disable';
        const control = this.dashboardForm.get('refreshIntervalSeconds');
        control.setValue(val ? this.refreshIntervals[0].seconds : null);
        control[action]();
      });

    this.listeners.push(sub);
  }

  ngOnDestroy() {
    this.progressSub.unsubscribe();
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  async ngOnInit() {
    this.dashboard = this.data.dashboard;

    this.dashboardForm.patchValue({
      name: this.dashboard.name,
      description: this.dashboard.description,
      categoryId: this.dashboard.categoryId,
      autoRefreshEnabled: Boolean(this.dashboard.autoRefreshEnabled),
      refreshIntervalSeconds: this.dashboard.refreshIntervalSeconds
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
    if (this.dashboard.categoryId) { return; }

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

    assign(this.dashboard, this.dashboardForm.value);
    this.observe.saveDashboard(this.dashboard).subscribe(
      data => {
        this.closeDashboard(data);
      }
    );
  }
}
