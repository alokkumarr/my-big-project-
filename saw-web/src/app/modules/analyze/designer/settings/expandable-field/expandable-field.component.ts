import { Component, Input, Output, EventEmitter } from '@angular/core';
import {
  ArtifactColumn,
  ArtifactColumnChart,
  AnalysisType,
  DesignerChangeEvent
} from '../../types';
import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  COMBO_TYPES,
  COMBO_TYPES_OBJ,
  TSCOMBO_TYPES,
  TSCOMBO_TYPES_OBJ
} from '../../../consts';
import { getArtifactColumnTypeIcon } from '../../utils';
import * as filter from 'lodash/filter';

@Component({
  selector: 'expandable-field',
  templateUrl: './expandable-field.component.html',
  styleUrls: ['./expandable-field.component.scss']
})
export class ExpandableFieldComponent {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumn;
  @Input() public analysisType: AnalysisType;
  @Input() public fieldCount: any;
  @Input() public sqlBuilder;
  @Input() analysisSubtype: string;
  public enablePercentByRow = true;

  AGGREGATE_TYPES = AGGREGATE_TYPES;
  AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
  public isExpanded = false;
  comboTypes = COMBO_TYPES;
  comboTypesObj = COMBO_TYPES_OBJ;

  getComboIcon(comboType) {
    // Since there are some old analyses taht are wrongly using the TSCOMBO_TYPES,
    // we have to add this hack
    // when selects another comboType, it will be properly selected from COMBO_TYPES
    // There is no real reason to use TSCOMBO_TYPES
    if (COMBO_TYPES.map(({ value }) => value).includes(comboType)) {
      return COMBO_TYPES_OBJ[comboType].icon;
    } else if (TSCOMBO_TYPES.map(({ value }) => value).includes(comboType)) {
      return TSCOMBO_TYPES_OBJ[comboType].icon;
    }
    return '';
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

  getArtifactColumnTypeIcon(artifactColumn) {
    return getArtifactColumnTypeIcon(artifactColumn);
  }

  toggleExpansion() {
    this.isExpanded = !this.isExpanded;
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

  onComboTypeChange(comboType) {
    this.enablePercentByRow = comboType === 'column' ? true : false;
    (this.artifactColumn as ArtifactColumnChart).comboType = comboType;
    this.change.emit({ subject: 'comboType', column: this.artifactColumn });
  }
}
