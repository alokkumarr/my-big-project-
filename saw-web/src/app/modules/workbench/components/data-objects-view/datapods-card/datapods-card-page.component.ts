import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'datapods-card-page',
  templateUrl: './datapods-card-page.component.html',
  styleUrls: ['./datapods-card-page.component.scss']
})
export class DatapodsCardPageComponent implements OnInit, OnDestroy {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  public updaterSubscribtion: any;
  public dataPods: Array<any> = [];

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
    this.dataPods = data;
    setTimeout(() => {}, 1000);
  }

  viewDetails(metadata) {
    // this.workbench.navigateToDetails(metadata);
  }
}
