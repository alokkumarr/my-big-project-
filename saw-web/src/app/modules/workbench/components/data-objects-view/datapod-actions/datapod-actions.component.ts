import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

import { WorkbenchService } from '../../../services/workbench.service';

const style = require('./datapod-actions.component.scss');
@Component({
  selector: 'datapod-actions',
  templateUrl: './datapod-actions.component.html',
  styles: [style]
})
export class DatapodActionsComponent implements OnInit {
  @Input() dpMetadata: any;

  constructor(private router: Router, public workBench: WorkbenchService) {}

  ngOnInit() {}

  gotoEdit(): void {
    this.workBench.setDataToLS('dpID', this.dpMetadata.id);
    this.router.navigate(['workbench', 'semantic', 'update']);
  }

  openSQLEditor(): void {
    if (this.dpMetadata.asOfNow.status === 'SUCCESS') {
      this.workBench.setDataToLS('dpMetadata', this.dpMetadata);
      this.router.navigate(['workbench', 'create', 'sql']);
    }
  }
}
