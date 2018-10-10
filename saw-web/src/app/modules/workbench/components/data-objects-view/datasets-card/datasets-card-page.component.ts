import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'datasets-card-page',
  templateUrl: './datasets-card-page.component.html',
  styleUrls: ['./datasets-card-page.component.scss']
})
export class DatasetsCardPageComponent implements OnInit, OnDestroy {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  public updaterSubscribtion: any;
  public dataSets: Array<any> = [];

  constructor(public dialog: MatDialog, public workbench: WorkbenchService) {}

  ngOnInit() {
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    this.dataSets = data;
  }

  viewDetails(metadata) {
    this.workbench.navigateToDetails(metadata);
  }
}
