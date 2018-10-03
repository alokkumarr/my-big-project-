import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Component({
  selector: 'datapods-grid-page',
  templateUrl: './datapods-grid-page.component.html',
  styleUrls: ['./datapods-grid-page.component.scss']
})
export class DatapodsGridPageComponent implements OnInit, OnDestroy {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  public gridData: Array<any>;
  public updaterSubscribtion: any;

  ngOnInit() {
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    if (data.length !== 0) {
      setTimeout(() => {
        this.reloadDataGrid(data);
      });
    }
  }

  reloadDataGrid(data) {
    this.gridData = data;
  }

  viewDetails(metadata) {
    // this.workbench.navigateToDetails(metadata);
  }
}
