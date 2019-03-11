import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import * as isUndefined from 'lodash/isUndefined';
import * as debounce from 'lodash/debounce';
import { ArtifactColumnChart, DesignerChangeEvent } from '../../../types';

const LIMIT_DEBOUNCE_DELAY = 400;
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
  public isInTabletMode = false;

  constructor(breakpointObserver: BreakpointObserver) {
    breakpointObserver
      .observe([Breakpoints.Medium, Breakpoints.Small])
      .subscribe(result => {
        this.isInTabletMode = result.matches;
      });

    this.onLimitDataChange = debounce(
      this.onLimitDataChange,
      LIMIT_DEBOUNCE_DELAY
    );
  }

  ngOnInit() {
    this.limitType =
      this.artifactColumn.limitValue === null
        ? ''
        : this.artifactColumn.limitType;
    this.limitValue = this.artifactColumn.limitValue;
  }

  onLimitDataChange(value, type) {
    console.log('limit changes', value);
    this.limitValue = value;
    this.limitType = type;
    if (!this.limitType) {
      return;
    }
    if (this.limitValue === null || isUndefined(this.limitValue)) {
      delete this.artifactColumn.limitValue;
      delete this.artifactColumn.limitType;
      return;
    }
    this.artifactColumn.limitValue = this.limitValue;
    this.artifactColumn.limitType = this.limitType;
    this.change.emit({ subject: 'fetchLimit' });
  }
}
