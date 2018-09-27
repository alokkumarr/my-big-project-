import { Component, OnInit, Input } from '@angular/core';
import { Router } from '@angular/router';

import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datapod-actions.component.html');
const style = require('./datapod-actions.component.scss');
@Component({
  selector: 'datapod-actions',
  template,
  styles: [style]
})
export class DatapodActionsComponent implements OnInit {
  @Input()
  dpMetadata: any;

  constructor(private router: Router, private workBench: WorkbenchService) {}

  ngOnInit() {}

  gotoEdit(): void {
    this.workBench.setDataToLS('dpID', this.dpMetadata.id);
    this.router.navigate(['workbench', 'semantic', 'update']);
  }
}
