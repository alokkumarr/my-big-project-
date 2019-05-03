import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import * as map from 'lodash/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as split from 'lodash/split';
import * as cloneDeep from 'lodash/cloneDeep';
import * as startsWith from 'lodash/startsWith';
import * as includes from 'lodash/includes';
import * as some from 'lodash/some';
// import * as startsWith from 'lodash/startsWith';
import { ConfirmDialogComponent } from '../../../../common/components/confirm-dialog';

import { ANALYSIS_METHODS } from '../../consts';
const methodsMap = {
  chart: ANALYSIS_METHODS[0].children[0].children,
  map: ANALYSIS_METHODS[0].children[3].children
};

@Component({
  selector: 'analysis-sub-type-chooser',
  templateUrl: 'analysis-sub-type-chooser.component.html',
  styleUrls: ['analysis-sub-type-chooser.component.scss']
})
export class AnalysisSubTypeChooserComponent implements OnInit {
  @Output() change = new EventEmitter();
  @Input() category: 'map' | 'chart';
  @Input() subType: string;
  @Input('supports') set setSupports(supports: string[]) {
    this.supports = fpPipe(
      fpFlatMap(support => support.children),
      fpMap('type'),
      fpMap(type => split(type, ':')),
      fpMap(([_, subType]) => subType)
    )(supports);
  }

  public supports;
  public subTypes;

  constructor(private _dialog: MatDialog) {}

  ngOnInit() {
    const methods = cloneDeep(methodsMap[this.category]) || [];
    this.subTypes = map(methods, chartType => {
      const [, type] = split(chartType.type, ':');

      const ret = {
        ...chartType,
        type,
        disabled: !this.doesSupportType(type, this.category)
      };
      return ret;
    });
  }

  doesSupportType(type, category) {
    if (category === 'chart') {
      return true;
    }
    if (category === 'map') {
      const isMapChart = startsWith(type, 'chart');
      const isMap = type === 'map';
      if (isMap) {
        const supportsMap = includes(this.supports, 'map');
        return supportsMap;
      } else if (isMapChart) {
        const supportsMapChart = some(this.supports, support =>
          startsWith(support, 'chart')
        );
        return supportsMapChart;
      }
    }
  }

  onChange(event) {
    const newValue = event.value;
    const oldValue = this.subType;
    this.subType = null;

    setTimeout(() => {
      this.subType = oldValue;
    });
    this.openConfirmationDialog()
      .afterClosed()
      .subscribe(isConfirmed => {
        if (isConfirmed) {
          this.change.emit(newValue);
        }
      });
  }

  openConfirmationDialog() {
    const resetConfirmation = {
      title: 'Your settings will be lost.',
      content: 'Are you sure you want to proceed?',
      positiveActionLabel: 'Proceed',
      negativeActionLabel: 'Cancel'
    };
    return this._dialog.open(ConfirmDialogComponent, {
      width: 'auto',
      height: 'auto',
      data: resetConfirmation
    } as MatDialogConfig);
  }
}
