import { Component, OnInit, Inject } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA } from '@angular/material';

const template = require('./save-dashboard.component.html');
require('./save-dashboard.component.scss');

@Component({
  selector: 'save-dashboard',
  template
})
export class SaveDashboardComponent implements OnInit {

  private dashboard: any;

  constructor(private dialogRef: MdDialogRef<SaveDashboardComponent>,
    @Inject(MD_DIALOG_DATA) private data: any) { }

  ngOnInit() {
    this.dashboard = this.data.dashboard;
  }

  closeDashboard(data) {
    this.dialogRef.close(data);
  }

  saveDashboard() {
    this.closeDashboard({});
  }
}
