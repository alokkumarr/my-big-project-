import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import * as isUndefined from 'lodash/isUndefined';
import * as debounce from 'lodash/debounce';
import { Store } from '@ngxs/store';
import { ArtifactColumnChart, DesignerChangeEvent } from '../../../types';
import { DesignerUpdateArtifactColumn } from '../../../actions/designer.actions';

const MAX_LIMIT = 99;
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

  constructor(private _store: Store, breakpointObserver: BreakpointObserver) {
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
    this.limitValue = value;
    this.limitType = type;
    this.resetInvalidValueIfNeeded(this.limitValue);
    if (
      !this.limitType ||
      (this.limitValue === null || isUndefined(this.limitValue))
    ) {
      return;
    }
    this.emitChange(this.limitType, this.limitValue);
  }

  onToggleClicked(limitType) {
    if (this.limitType === limitType) {
      this.limitType = null;
      this.limitValue = null;
      this.emitChange(this.limitType, this.limitValue);
    }
  }

  emitChange(limitType, limitValue) {
    const { table, columnName } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        limitValue,
        limitType
      })
    );
    this.change.emit({ subject: 'fetchLimit' });
  }

  resetInvalidValueIfNeeded(value) {
    const int = parseInt(value, 10);
    if (int < 0) {
      this.limitValue = 0;
    } else if (int > MAX_LIMIT) {
      this.limitValue = MAX_LIMIT;
    }
  }
}
