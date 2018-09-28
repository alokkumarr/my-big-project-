import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

const template = require('./datapods-grid-page.component.html');
const style = require('./datapods-grid-page.component.scss');

@Component({
  selector: 'datapods-grid-page',
  template,
  styles: [
    `:host {
      width: 100%;
      height: 100%;
      overflow: hidden;
    }`,
    style
  ]
})
export class DatapodsGridPageComponent implements OnInit, OnDestroy {
  @Input()
  searchTerm: string;
  @Input()
  updater: BehaviorSubject<any>;
  private gridData: Array<any>;
  private updaterSubscribtion: any;

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
