import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
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
  public hasDateInterval = false;
  public isDataField = false;
  public colorPickerConfig = {};

  constructor(private _store: Store) {
    this.onAliasChange = debounce(this.onAliasChange, ALIAS_CHANGE_DELAY);
  }

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.isDataField = ['y', 'z', 'data'].includes(
      (<ArtifactColumnChart>this.artifactColumn).area
    );

    this.typeIcon = getArtifactColumnTypeIcon(
      this.artifactColumn,
      this.analysisType,
      this.analysisSubtype
    );
    this.colorPickerConfig = cloneDeep(DEFAULT_COLOR_PICKER_OPTION);
    set(
      this.colorPickerConfig,
      'bgColor',
      this.artifactColumn.seriesColor || '#ffffff'
    );
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
      set(this.artifactColumn, 'seriesColor', event.data.color);
      this.change.emit({
        subject: 'seriesColorChange',
        data: { artifact: this.artifactColumn }
      });
    }
  }
}
