import { Component, Input } from '@angular/core';
import * as groupBy from 'lodash/groupBy';

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
    const groupedColumns = groupBy(columns, 'area');
    const { data, column, row } = groupedColumns;
    this.dataFields = data || [];
    this.columnFields = column || [];
    this.rowFields = row || [];
  }
}
