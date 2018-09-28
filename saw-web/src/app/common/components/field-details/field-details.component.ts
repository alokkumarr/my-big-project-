import { Component, Input } from '@angular/core';
import * as forEach from 'lodash/forEach';

const style = require('./field-details.component.scss');

@Component({
  selector: 'field-details',
  templateUrl: './field-details.component.html',
  styles: [
    `:host {
      display: grid;
      margin-bottom: 10px;
    }`,
    style
  ]
})
export class FieldDetailsComponent {
  public dataFields: any[] = [];
  public columnFields: any[] = [];
  public rowFields: any[] = [];

  @Input()
  set artifactColumns(columns) {
    if (!Array.isArray(columns)) { return; }

    this.dataFields = [];
    this.columnFields = [];
    this.rowFields = [];

    forEach(columns, col => {
      if (col.area === 'data') {
        this.dataFields = [...this.dataFields, ...col];
      }
      if (col.area === 'column') {
        this.columnFields = [...this.columnFields, ...col];
      }
      if (col.area === 'row') {
        this.rowFields = [...this.rowFields, ...col];
      }
    });
  }
}
