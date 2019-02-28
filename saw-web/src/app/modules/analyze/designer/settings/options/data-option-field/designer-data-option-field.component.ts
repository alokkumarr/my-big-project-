import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import {
  COMBO_TYPES,
  DATE_TYPES
} from '../../../../consts';
import {
  ArtifactColumn,
  DesignerChangeEvent,
  SqlBuilder,
  ArtifactColumnChart
} from '../../../types';

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

  public comboTypes = COMBO_TYPES;
  public hasDateInterval = false;
  public isDataField = false;

  constructor() { }

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.isDataField = ['y', 'z'].includes((<ArtifactColumnChart>this.artifactColumn).area);
  }

  onAliasChange(alias) {
    this.artifactColumn.alias = alias;
    this.change.emit({subject: 'aliasName'});
  }

  onAggregateChange(value) {
    this.comboTypes = filter(COMBO_TYPES, type => {
      if (value === 'percentageByRow' && type.value === 'column') {
        return true;
      }
      if (value !== 'percentageByRow') {
        return true;
      }
    });
    this.artifactColumn.aggregate = value;
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
}
