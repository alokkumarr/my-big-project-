import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as isUndefined from 'lodash/isUndefined';
import {
  ArtifactColumnChart,
  DesignerChangeEvent
} from '../../../types';
@Component({
  selector: 'designer-data-limit-selector',
  templateUrl: 'designer-data-limit-selector.component.html',
  styleUrls: ['designer-data-limit-selector.component.scss']
})

export class DesignerDataLimitSelectorComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumnChart;
  public limitType;
  public limitValue;

  constructor() { }

  ngOnInit() {
    this.limitType =
      this.artifactColumn.limitValue === null
        ? ''
        : this.artifactColumn.limitType;
    this.limitValue = this.artifactColumn.limitValue;
  }

  onLimitDataChange() {
    this.limitValue = this.limitValue < 0 ? '' : this.limitValue;
    if (
      this.limitValue < 0 ||
      isUndefined(this.limitType) ||
      this.limitType === null
    ) {
      return false;
    }
    if (this.limitValue === null || isUndefined(this.limitValue)) {
      delete this.artifactColumn.limitValue;
      delete this.artifactColumn.limitType;
    }
    this.artifactColumn.limitValue = this.limitValue;
    this.artifactColumn.limitType = this.limitType;
    this.change.emit({ subject: 'fetchLimit' });
  }
}
