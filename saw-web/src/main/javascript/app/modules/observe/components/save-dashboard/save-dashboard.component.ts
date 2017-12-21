import { Component, OnInit, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';
import { ObserveService } from '../../services/observe.service';

const template = require('./save-dashboard.component.html');
require('./save-dashboard.component.scss');

@Component({
  selector: 'save-dashboard',
  template
})
export class SaveDashboardComponent implements OnInit {

  private dashboard: any;
  public categories = [{
    id: '1',
    name: 'Category 1'
  }, {
    id: '2',
    name: 'Category 2'
  }, {
    id: '3',
    name: 'Category 3'
  }];

  constructor(private dialogRef: MdDialogRef<SaveDashboardComponent>,
    @Inject(MD_DIALOG_DATA) private data: any,
    private observe: ObserveService
  ) { }

  ngOnInit() {
    this.dashboard = this.data.dashboard;
    this.dashboard.categoryId = this.categories[0].id;
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
      this.closeDashboard(true);
    }, err => {
    });
  }
}
