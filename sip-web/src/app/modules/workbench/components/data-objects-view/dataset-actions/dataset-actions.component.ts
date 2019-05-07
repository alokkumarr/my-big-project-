import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'dataset-actions',
  templateUrl: './dataset-actions.component.html',
  styleUrls: ['./dataset-actions.component.scss']
})
export class DatasetActionsComponent implements OnInit {
  @Input() dsMetadata: any;

  constructor(private router: Router, public workBench: WorkbenchService) {}

  ngOnInit() {}

  openSQLEditor(): void {
    if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
      this.workBench.setDataToLS('dsMetadata', this.dsMetadata);
      this.router.navigate(['workbench', 'create', 'sql']);
    }
  }
}
