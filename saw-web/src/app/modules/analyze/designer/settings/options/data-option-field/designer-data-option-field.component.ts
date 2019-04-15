import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as debounce from 'lodash/debounce';
import { Store } from '@ngxs/store';

import { DesignerUpdateArtifactColumn } from '../../../actions/designer.actions';
import { COMBO_TYPES, DATE_TYPES } from '../../../../consts';
import {
  ArtifactColumn,
  DesignerChangeEvent,
  SqlBuilder,
  ArtifactColumnChart,
  ArtifactColumnPivot
} from '../../../types';

const ALIAS_CHANGE_DELAY = 500;

@Component({
  selector: 'designer-data-option-field',
  templateUrl: 'designer-data-option-field.component.html',
  styleUrls: ['designer-data-option-field.component.scss']
})
export class DesignerDataOptionFieldComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumn;
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() sqlBuilder: SqlBuilder;
  @Input() fieldCount: number;

  public comboTypes = COMBO_TYPES;
  public hasDateInterval = false;
  public isDataField = false;

  constructor(private _store: Store) {
    this.onAliasChange = debounce(this.onAliasChange, ALIAS_CHANGE_DELAY);
  }

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.isDataField = ['y', 'z', 'data'].includes(
      (<ArtifactColumnChart>this.artifactColumn).area
    );
  }

  onAliasChange(aliasName) {
    const { table, columnName } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        aliasName
      })
    );
    this.change.emit({ subject: 'aliasName' });
  }

  onAggregateChange(aggregate) {
    this.comboTypes = filter(COMBO_TYPES, type => {
      if (aggregate === 'percentageByRow' && type.value === 'column') {
        return true;
      }
      if (aggregate !== 'percentageByRow') {
        return true;
      }
    });
    const { table, columnName } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        aggregate
      })
    );
    this.change.emit({ subject: 'aggregate', column: this.artifactColumn });
  }

  /**
   * asChartColumn - Typecasts artifact column to ArtifactColumnChart
   * For use in templates. Angular's AOT compiler is strict about types.
   *
   * @param column
   * @returns {ArtifactColumnChart}
   */
  asChartColumn(column): ArtifactColumnChart {
    return column;
  }

  asPivotColumn(column): ArtifactColumnPivot {
    return column;
  }
}
