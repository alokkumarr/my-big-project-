import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';

@Component({
  selector: 'designer-map',
  templateUrl: 'designer-map.component.html'
  // styleUrls: ['designer-map.component.scss']
})
export class DesignerMapComponent implements OnInit {
  data: Array<any>;
  sqlBuilder;
  auxSettings: any = {};

  public chartOptions = {};
  public chartUpdater = new Subject();

  @Input() actionBus;

  @Input('sqlBuilder')
  set setSqlBuilder(sqlBuilder) {
    this.sqlBuilder = sqlBuilder;
  }

  @Input('auxSettings')
  set setAuxSettings(settings) {
    this.auxSettings = settings;
  }

  @Input('data')
  set setData(d) {
    if (!d) {
      return;
    }
    this.data = d;
  }

  ngOnInit() {

  }

}
