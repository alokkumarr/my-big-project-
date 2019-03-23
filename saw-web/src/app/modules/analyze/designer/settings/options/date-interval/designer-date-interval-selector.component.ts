import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ArtifactColumnPivot, DesignerChangeEvent } from '../../../types';
import { DATE_INTERVALS, DEFAULT_DATE_FORMAT } from '../../../../consts';

@Component({
  selector: 'designer-date-interval-selector',
  templateUrl: 'designer-date-interval-selector.component.html'
})
export class DesignerDateIntervalSelectorComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumnPivot;
  public DATE_INTERVALS = DATE_INTERVALS;
  constructor() {}

  ngOnInit() {}

  onDateIntervalChange(value) {
    this.artifactColumn.dateInterval = value;
    if (this.artifactColumn.dateInterval !== 'day') {
      this.artifactColumn.format = DEFAULT_DATE_FORMAT.value;
      this.artifactColumn.dateFormat = DEFAULT_DATE_FORMAT.value;
    }
    this.change.emit({ subject: 'dateInterval' });
  }
}
