import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'designer-settings-aux-map-chart',
  templateUrl: 'aux-map-chart.component.html'
})

export class DesignerSettingsAuxMapChartComponent implements OnInit {

  @Input() chartTitle;
  @Output() change = new EventEmitter();
  constructor() { }

  ngOnInit() { }
}
