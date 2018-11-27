import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';
import * as isString from 'lodash/isString';
import {FormControl, FormBuilder, FormGroup} from '@angular/forms';
import {Observable} from 'rxjs';
import {map, startWith} from 'rxjs/operators';
import { ArtifactColumnChart, Format, Region } from '../../../types';
import {
  DATE_INTERVALS,
  DATE_TYPES,
  CHART_DATE_FORMATS,
  CHART_DATE_FORMATS_OBJ
} from '../../../../consts';
import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';
import {
  formatNumber,
  isFormatted
} from '../../../../../../common/utils/numberFormatter';
import { getMapDataIndexByGeoType } from '../../../../../../common/components/charts/map-data-index';

import { DesignerChangeEvent } from '../../../types';

const FLOAT_SAMPLE = 1000.33333;
const INT_SAMPLE = 1000;
@Component({
  selector: 'expand-detail-chart',
  templateUrl: 'expand-detail-chart.component.html'
})
export class ExpandDetailChartComponent implements OnInit {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();

  @Input() public artifactColumn: ArtifactColumnChart;
  @Input() public fieldCount: any;

  public DATE_INTERVALS = DATE_INTERVALS;
  public DATE_FORMATS_OBJ = CHART_DATE_FORMATS_OBJ;
  public isDataField = false;
  public hasDateInterval = false;
  public numberSample: string;
  public dateSample: string;
  public isFloat: boolean;
  public limitType;
  public limitValue;
  public stateForm: FormGroup = this.fb.group({
    regionCtrl: '',
  });
  public regionCtrl = new FormControl();
  filteredRegions: Observable<Region>;

  constructor(
    private _analyzeDialogService: AnalyzeDialogService,
    private fb: FormBuilder
    ) {}

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.limitType =
      this.artifactColumn.limitValue === null
        ? ''
        : this.artifactColumn.limitType;
    this.limitValue = this.artifactColumn.limitValue;

    this.isDataField = ['y', 'z'].includes(this.artifactColumn.area);
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.changeSample();

    this.filteredRegions = this.stateForm.get('regionCtrl').valueChanges
      .pipe(
        startWith({name: ''}),
        map(value => {
          if (isString(value)) {
            return this._filterRegions(value);
          }
          return this._filterRegions(value.name);
        })
      );
  }

  onRegionSelected(region) {
    this.artifactColumn.region = region;
    this.change.emit({ subject: 'region' });
  }

  displayWithRegion(option) {
    return option.name;
  }

  private _filter = (array, name) => {
    const filterValue = name.toLowerCase();

    return array.filter(item => item.name.toLowerCase().indexOf(filterValue) === 0);
  }

  private _filterRegions(value) {
    const mapDataIndex = getMapDataIndexByGeoType(this.artifactColumn.geoType);
    if (value) {
      return mapDataIndex
        .map(group => ({name: group.name, children: this._filter(group.children, value)}))
        .filter(group => group.children.length > 0);
    }

    return mapDataIndex;
  }

  onAliasChange(value) {
    this.artifactColumn.aliasName = value;
    this.change.emit({ subject: 'aliasName' });
  }

  onFormatChange(format: Format | string) {
    if (format) {
      this.artifactColumn.format = format;
      this.artifactColumn.dateFormat = format as string;
      this.changeSample();
      this.change.emit({ subject: 'format' });
    }
  }

  openDateFormatDialog() {
    this._analyzeDialogService
      .openDateFormatDialog(
        <string>this.artifactColumn.dateFormat,
        CHART_DATE_FORMATS
      )
      .afterClosed()
      .subscribe(format => this.onFormatChange(format));
  }

  changeSample() {
    if (this.isDataField) {
      this.changeNumberSample();
    } else if (this.hasDateInterval) {
      this.changeDateSample();
    }
  }

  changeNumberSample() {
    const format = this.artifactColumn.format;
    const sampleNr = this.isFloat ? FLOAT_SAMPLE : INT_SAMPLE;

    if (format && isFormatted(<Format>format)) {
      this.numberSample = formatNumber(sampleNr, <Format>format);
    } else {
      this.numberSample = null;
    }
  }

  changeDateSample() {
    const format = <string>this.artifactColumn.format;
    if (format) {
      this.dateSample = moment.utc().format(format);
    }
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
