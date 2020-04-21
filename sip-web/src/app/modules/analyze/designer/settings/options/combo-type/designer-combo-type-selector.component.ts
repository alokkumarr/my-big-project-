import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Store } from '@ngxs/store';
import {
  COMBO_TYPES,
  COMBO_TYPES_OBJ,
  TSCOMBO_TYPES,
  TSCOMBO_TYPES_OBJ
} from '../../../../consts';
import {
  ArtifactColumn,
  DesignerChangeEvent,
  ArtifactColumnChart
} from '../../../types';
import { DesignerUpdateArtifactColumn } from '../../../actions/designer.actions';

@Component({
  selector: 'designer-combo-type-selector',
  templateUrl: 'designer-combo-type-selector.component.html'
})
export class DesignerComboTypeSelectorComponent {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumn;
  @Input() public comboTypes;

  public enablePercentByRow = true;

  constructor(private _store: Store) {}

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

  onComboTypeChange(comboType) {
    this.enablePercentByRow = comboType === 'column' ? true : false;
    (<any>this.artifactColumn).displayType = comboType;
    const { table, columnName, dataField } = this.artifactColumn;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        table,
        columnName,
        dataField,
        displayType: comboType
      })
    );
    this.change.emit({ subject: 'comboType', column: this.artifactColumn });
  }

  get comboType(): string {
    if (!this.artifactColumn) {
      return '';
    }

    return (
      (<ArtifactColumnChart>this.artifactColumn).comboType ||
      (<any>this.artifactColumn).displayType
    );
  }

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
}
