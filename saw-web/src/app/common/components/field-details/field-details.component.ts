import { Component, Input } from '@angular/core';
import * as forEach from 'lodash/forEach';

@Component({
  selector: 'field-details',
  templateUrl: './field-details.component.html',
  styleUrls: ['./field-details.component.scss']
})
export class FieldDetailsComponent {
  public dataFields: any[] = [];
  public columnFields: any[] = [];
  public rowFields: any[] = [];

  @Input()
  set artifactColumns(columns) {
    if (!Array.isArray(columns)) {
      return;
    }

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
