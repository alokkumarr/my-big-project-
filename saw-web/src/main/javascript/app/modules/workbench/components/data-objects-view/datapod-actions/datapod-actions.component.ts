import { Component, OnInit, Input } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datapod-actions.component.html');
require('./datapod-actions.component.scss');
@Component({
  selector: 'datapod-actions',
  template,
  styles: []
})
export class DatapodActionsComponent implements OnInit {
  @Input()
  dpMetadata: any;

  constructor(private router: UIRouter, private workBench: WorkbenchService) {}

  ngOnInit() {}

  gotoEdit(): void {
    this.workBench.setDataToLS('dpID', this.dpMetadata.id);
    this.router.stateService.go('workbench.updateSemantic');
  }
}
