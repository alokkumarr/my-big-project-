
import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

import { WorkbenchService } from '../../services/workbench.service';

const template = require('./dataset-actions.component.html');
require('./dataset-actions.component.scss');
@Component({
  selector: 'dataset-actions',
  template,
  styles: []
})

export class DatasetActionsComponent implements OnInit {
  @Input() dsMetadata: any;

  constructor(
    private router: Router,
    private workBench: WorkbenchService
  ) { }

  ngOnInit() { }

  openSQLEditor(): void {
    if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
      this.workBench.setDataToLS('dsMetadata', this.dsMetadata);
      this.router.navigate(['workbench', 'create', 'sql']);
    }
  }
}
