import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

import { nonEmpty } from '../../../validators/non-empty.validator';

import { Subscription } from 'rxjs';

import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as assign from 'lodash/assign';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as isUndefined from 'lodash/isUndefined';
import * as toNumber from 'lodash/toNumber';

import * as moment from 'moment';
import { requireIf } from '../../../validators/required-if.validator';
import {
  DATE_FORMAT,
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS,
  KPI_AGGREGATIONS,
  KPI_BG_COLORS
} from '../../../consts';

@Component({
  selector: 'widget-kpi',
  templateUrl: './widget-kpi.component.html',
  styleUrls: ['./widget-kpi.component.scss']
})
export class WidgetKPIComponent implements OnInit, OnDestroy {
  _kpi: any;
  _metric: any;
  _kpiType: string;
  bandPaletteValue: string;
  kpiBgColorValue: string;

  @Output() onKPIAction = new EventEmitter();

  dateFilters = DATE_PRESETS;
  showDateFields = false;
  aggregations = KPI_AGGREGATIONS;
  kpiBgColors = KPI_BG_COLORS;

  kpiForm: FormGroup;
  datePresetSubscription: Subscription;
  primaryAggregationSubscription: Subscription;

  constructor(private fb: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {}

  ngOnDestroy() {
    if (this.datePresetSubscription) {
      this.datePresetSubscription.unsubscribe();
    }
    if (this.primaryAggregationSubscription) {
      this.primaryAggregationSubscription.unsubscribe();
    }
  }

  /**
   * Initialises form and adds listeners to individual form fields as required
   */
  createForm() {
    const secAggregateControls = {};
    forEach(this.aggregations, ag => {
      secAggregateControls[ag.value] = [false];
    });

    this.kpiForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(30), nonEmpty()]],
      dateField: ['', Validators.required],
      gte: [
        moment(),
        [requireIf('filter', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ],
      lte: [
        moment(),
        [requireIf('filter', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ],
      filter: [this.dateFilters[0].value, Validators.required],
      primAggregate: [this.aggregations[0].value, Validators.required],
      secAggregates: this.fb.group(secAggregateControls),
      target: [0, [Validators.required, nonEmpty()]],
      measure1: [''],
      measure2: [''],
      kpiBgColor: ['blue', Validators.required]
    });

    /* Only show date inputs if custom filter is selected */
    this.datePresetSubscription = this.kpiForm
      .get('filter')
      .valueChanges.subscribe(data => {
        this.kpiForm.get('lte').updateValueAndValidity();
        this.kpiForm.get('gte').updateValueAndValidity();
        this.showDateFields = data === CUSTOM_DATE_PRESET_VALUE;
      });

    /* Update disablity status of secondary aggregations based on
     * primary aggregation selection */
    this.primaryAggregationSubscription = this.kpiForm
      .get('primAggregate')
      .valueChanges.subscribe(this.updateSecondaryAggregations.bind(this));
  }

  get kpiName() {
    return this.kpiForm.get('name');
  }

  /**
   * On every change to primary aggregation, disable that control
   * in secondary controls and clear its value. Primary aggregation
   * cannot also be a part of secondary aggregations.
   */
  updateSecondaryAggregations(primaryAggregation: string) {
    const secondaryAggregatesForm = this.kpiForm.get(
      'secAggregates'
    ) as FormGroup;

    forEach(this.aggregations, ag => {
      const aggregateControl = secondaryAggregatesForm.get(ag.value);
      if (ag.value === primaryAggregation) {
        aggregateControl.setValue(false);
        aggregateControl.disable();
      } else {
        aggregateControl.enable();
      }
    });
  }

  /**
   * Metric is required to set default date field (if not present)
   * and to populate date field selector in form.
   */
  @Input()
  set metric(data: any) {
    if (!data) {
      return;
    }
    this._metric = data;
    const kpiDateField = get(this._kpi, 'filters.0.columnName');

    this.kpiForm
      .get('dateField')
      .setValue(kpiDateField || data.dateColumns[0].columnName);
  }
  /**
   * Type is required to support normal KPI's and bullet KPI with the same component
   */
  @Input()
  set type(data: any) {
    if (!data) {
      return;
    }
    this._kpiType = data;
  }

  /**
   * Updates the form with the data present in kpi structure
   */
  @Input()
  set kpi(data: any) {
    if (!data) {
      return;
    }

    this._kpi = data;

    data.name && this.kpiForm.get('name').setValue(data.name);

    const target = get(data, 'target');
    target && this.kpiForm.get('target').setValue(target);

    const measure1 = get(data, 'measure1');
    measure1 && this.kpiForm.get('measure1').setValue(measure1);

    const measure2 = get(data, 'measure2');
    measure2 && this.kpiForm.get('measure2').setValue(measure2);

    this.kpiBgColorValue = isUndefined(data.kpiBgColor)
      ? 'black'
      : data.kpiBgColor;

    this.bandPaletteValue = isUndefined(data.bulletPalette)
      ? 'rog'
      : data.bulletPalette;

    const dateField = get(data, 'filters.0.columnName');
    dateField && this.kpiForm.get('dateField').setValue(dateField);

    const filt = get(data, 'filters.0.model.preset');
    this.kpiForm.get('filter').setValue(filt || this.dateFilters[0].value);

    const lte = get(data, 'filters.0.model.lte');
    lte &&
      this.kpiForm
        .get('lte')
        .setValue(moment(lte, DATE_FORMAT.YYYY_MM_DD_HH_mm_ss));

    const gte = get(data, 'filters.0.model.gte');
    gte &&
      this.kpiForm
        .get('gte')
        .setValue(moment(gte, DATE_FORMAT.YYYY_MM_DD_HH_mm_ss));

    const [primaryAggregate, ...secondaryAggregates] = get(
      data,
      'dataFields.0.aggregate',
      []
    );
    this.kpiForm
      .get('primAggregate')
      .setValue(primaryAggregate || this.aggregations[0].value);

    const secAggregateForm = this.kpiForm.get('secAggregates') as FormGroup;
    forEach(this.aggregations, ag => {
      secAggregateForm
        .get(ag.value)
        .setValue(secondaryAggregates.includes(ag.value));
    });
  }

  /**
   * Returns the model for date filter. If custom preset has been chosen,
   * includes the custom date range in result as well.
   */
  prepareDateFilterModel() {
    const model = {
      preset: this.kpiForm.get('filter').value
    };

    if (model.preset !== CUSTOM_DATE_PRESET_VALUE) {
      return model;
    }

    // Adding static time signatures until we allow users to choose time
    // for `to` and `from` fields.
    return {
      ...model,
      lte:
        this.kpiForm.get('lte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 23:59:59',
      gte:
        this.kpiForm.get('gte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 00:00:00'
    };
  }

  /**
   * Converts the form values to backend-valid structure and notifies parent.
   * Represensts the save/update operation.
   */
  applyKPI() {
    const dataField = get(this._kpi, 'dataFields.0');
    const dateField = find(
      this._metric.dateColumns,
      col => col.columnName === this.kpiForm.get('dateField').value
    );
    const secondaryAggregates = filter(
      this.aggregations,
      ag => this.kpiForm.get('secAggregates').get(ag.value).value
    );

    this.onKPIAction.emit({
      kpi: assign({}, this._kpi, {
        name: this.kpiForm.get('name').value,
        target: toNumber(this.kpiForm.get('target').value),
        measure1: toNumber(this.kpiForm.get('measure1').value),
        measure2: toNumber(this.kpiForm.get('measure2').value),
        kpiBgColor: this.kpiBgColorValue,
        bulletPalette: this.bandPaletteValue,
        dataFields: [
          {
            columnName: dataField.columnName,
            name: dataField.name,
            displayName: dataField.displayName,
            aggregate: [
              this.kpiForm.get('primAggregate').value,
              ...map(secondaryAggregates, ag => ag.value)
            ]
          }
        ],
        filters: [
          {
            type: dateField.type,
            columnName: dateField.columnName,
            model: this.prepareDateFilterModel()
          }
        ]
      })
    });
  }
}