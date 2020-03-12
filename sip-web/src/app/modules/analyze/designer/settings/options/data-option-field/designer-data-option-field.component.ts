import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as take from 'lodash/take';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as debounce from 'lodash/debounce';
import * as cloneDeep from 'lodash/cloneDeep';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpFilter from 'lodash/fp/filter';
import { Store } from '@ngxs/store';

import { DesignerUpdateArtifactColumn } from '../../../actions/designer.actions';
import { COMBO_TYPES, DATE_TYPES } from '../../../../consts';
import {
  ArtifactColumn,
  DesignerChangeEvent,
  ArtifactColumnChart,
  ArtifactColumnPivot
} from '../../../types';
import { QueryDSL } from 'src/app/models';
import { getArtifactColumnTypeIcon } from '../../../utils';
import { AggregateChooserComponent } from 'src/app/common/components/aggregate-chooser';
import { DEFAULT_COLOR_PICKER_OPTION } from '../../../../../../common/components/custom-color-picker/default-color-picker-options';
import { CHART_COLORS, NUMBER_TYPES } from 'src/app/common/consts';
import { DATA_AXIS } from '../../../consts';

const ALIAS_CHANGE_DELAY = 500;
const DEFAULT_CHART_COLORS = cloneDeep(CHART_COLORS);
const ADDITIONAL_PRESET_COLORS = [
  '#4e79a7',
  '#59a14f',
  '#9c755f',
  '#f28e2b',
  '#edc948',
  '#bab0ac',
  '#e15759',
  '#b07aa1',
  '#76b7b2',
  '#ff9da7'
];

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
  @Input('sipQuery') set setSipQuery(sipQuery: QueryDSL) {
    this.sipQuery = sipQuery;
    const fields = fpPipe(
      fpFlatMap(artifact => artifact.fields),
      fpFilter(artifact => artifact.area === 'y')
    )(sipQuery.artifacts);
    this.fieldCount = fields.length;
  }
  public typeIcon: string;
  public fieldCount: number;
  public sipQuery: QueryDSL;
  public comboTypes = COMBO_TYPES;
  public supportsDateInterval = false;
  public supportsDateFormat = false;
  public isDataField = false;
  public colorPickerConfig = {};

  constructor(private _store: Store) {
    this.onAliasChange = debounce(this.onAliasChange, ALIAS_CHANGE_DELAY);
  }

  ngOnInit() {
    const type = this.artifactColumn.type;

    this.supportsDateInterval =
      DATE_TYPES.includes(type) &&
      (this.analysisType === 'pivot' || this.analysisSubtype === 'comparison');

    this.supportsDateFormat =
      DATE_TYPES.includes(type) &&
      this.analysisSubtype !== 'comparison' && // no date formats supported in comparison chart
      (this.analysisType !== 'pivot' || // all charts
        this.asPivotColumn(this.artifactColumn).groupInterval === 'day'); // pivot only if day is selected

    this.isDataField = DATA_AXIS.includes(
      (<ArtifactColumnChart>this.artifactColumn).area
    );

    this.typeIcon = getArtifactColumnTypeIcon(
      this.artifactColumn,
      this.analysisType,
      this.analysisSubtype
    );
    this.colorPickerConfig = cloneDeep(DEFAULT_COLOR_PICKER_OPTION);
    const presetColors = [
      ...take(DEFAULT_CHART_COLORS, 11),
      ...ADDITIONAL_PRESET_COLORS
    ];
    set(this.colorPickerConfig, 'presetColors', presetColors);
    set(this.colorPickerConfig, 'iscustomStyleNeeded', true);
  }

  onAliasChange(alias) {
    const { table, columnName, dataField } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        alias,
        dataField
      })
    );
    this.change.emit({ subject: 'alias' });
  }

  onAggregateChange(aggregate) {
    this.comboTypes = filter(COMBO_TYPES, type => {
      if (aggregate === 'percentagebyrow' && type.value === 'column') {
        return true;
      }
      if (aggregate !== 'percentagebyrow') {
        return true;
      }
    });
    const { table, columnName, dataField } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        dataField,
        aggregate
      })
    );
    this.change.emit({ subject: 'aggregate', column: this.artifactColumn });
  }

  get chartDisplayType(): string {
    return (
      (<ArtifactColumnChart>this.artifactColumn).comboType ||
      (<any>this.artifactColumn).displayType
    );
  }

  shouldShowAggregate(): boolean {
    return (
      (this.analysisSubtype === 'scatter' &&
        NUMBER_TYPES.includes(this.artifactColumn.type)) ||
      Boolean(this.artifactColumn.aggregate)
    );
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

  checkChartType() {
    return AggregateChooserComponent.supportsPercentByRow(this.analysisSubtype);
  }

  selectedColor(event) {
    if (event.data) {
      set(this.artifactColumn, 'seriesColor', event.data);
      set(this.artifactColumn, 'colorSetFromPicker', true);
      this.change.emit({
        subject: 'seriesColorChange',
        data: { artifact: this.artifactColumn }
      });
    }
  }
}
