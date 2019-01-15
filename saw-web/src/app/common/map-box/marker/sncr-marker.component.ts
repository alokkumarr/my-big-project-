import { Component, Input } from '@angular/core';

@Component({
  selector: 'sncr-marker',
  templateUrl: 'sncr-marker.component.html',
  styleUrls: ['sncr-marker.component.scss']
})

export class SncrMarkerComponent {
  @Input() fields;
  @Input() data;

  ngOnInit() {
    console.log('data', this.data);
  }

  getListLabel(field, data) {
    const colName = field.columnName;
    return `${colName}: ${data[colName]}`;

  }
}
