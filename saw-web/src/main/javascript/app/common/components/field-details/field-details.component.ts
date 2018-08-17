import { Component, OnInit, Inject, Input } from '@angular/core';
import * as forEach from 'lodash/forEach';


const template = require('./field-details.component.html');
require('./field-details.component.scss');

@Component({
  selector: 'field-details',
  template
})

export class FieldDetailsComponent {
  @Input() artifactColumns;
  public dataFields: any[];
  public columnFields: any[];
  public rowFields: any[];

  ngOnInit() {
    this.dataFields = this.columnFields = this.rowFields = [];
    forEach(this.artifactColumns, col=> {
      if(col.area === 'data') {
        this.dataFields = [...this.dataFields , ...col];
      }
      if(col.area === 'column') {
        this.columnFields = [...this.columnFields , ...col];
      }
      if(col.area === 'row') {
        this.rowFields = [...this.rowFields , ...col];
      }
    })
  }
}
