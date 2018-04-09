import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

import { Subscription } from 'rxjs/Subscription';

import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as assign from 'lodash/assign';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';

import * as moment from 'moment';
import { requireIf } from '../../../validators/required-if.validator';
import { WIDGET_ACTIONS } from '../widget.model';
import {
  DATE_FORMAT,
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS_OBJ,
  DATE_PRESETS,
  KPI_AGGREGATIONS
} from '../../../consts';

const template = require('./widget-kpi.component.html');
require('./widget-kpi.component.scss');

@Component({
  selector: 'widget-kpi',
  template
})
export class WidgetKPIComponent implements OnInit, OnDestroy {
  _kpi: any;
  _metric: any;

  @Output() onKPIAction = new EventEmitter();

  dateFilters = DATE_PRESETS;
  showDateFields = false;
  aggregations = KPI_AGGREGATIONS;

  kpiForm: FormGroup;
  datePresetSubscription: Subscription;
  primaryAggregationSubscription: Subscription;

  constructor(private fb: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {}

  ngOnDestroy() {
    this.datePresetSubscription && this.datePresetSubscription.unsubscribe();
    this.primaryAggregationSubscription &&
      this.primaryAggregationSubscription.unsubscribe();
  }

  /**
   * Initialises form and adds listeners to individual form fields as required
   *
   * @memberof WidgetKPIComponent
   */
  createForm() {
    const secAggregateControls = {};
    forEach(this.aggregations, ag => {
      secAggregateControls[ag.value] = [false];
    });

    window['mykpi'] = this;

    this.kpiForm = this.fb.group({
      name: ['', Validators.required],
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
      secAggregates: this.fb.group(secAggregateControls)
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

  /**
   * On every change to primary aggregation, disable that control
   * in secondary controls and clear its value. Primary aggregation
   * cannot also be a part of secondary aggregations.
   *
   * @param {string} primaryAggregation
   * @memberof WidgetKPIComponent
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
   *
   * @memberof WidgetKPIComponent
   */
  @Input()
  set metric(data: any) {
    if (!data) return;
    this._metric = data;
    const kpiDateField = get(this._kpi, 'filters.0.columnName');

    this.kpiForm
      .get('dateField')
      .setValue(kpiDateField || data.dateColumns[0].columnName);
  }

  /**
   * Updates the form with the data present in kpi structure
   *
   * @memberof WidgetKPIComponent
   */
  @Input()
  set kpi(data: any) {
    if (!data) return;

    this._kpi = data;

    data.name && this.kpiForm.get('name').setValue(data.name);

    const dateField = get(data, 'filters.0.columnName');
    dateField && this.kpiForm.get('dateField').setValue(dateField);

    const filt = get(data, 'filters.0.model.preset');
    this.kpiForm.get('filter').setValue(filt || this.dateFilters[0].value);

    const lte = get(data, 'filters.0.model.lte');
    lte &&
      this.kpiForm.get('lte').setValue(moment(lte, DATE_FORMAT.YYYY_MM_DD));

    const gte = get(data, 'filters.0.model.gte');
    gte &&
      this.kpiForm.get('gte').setValue(moment(gte, DATE_FORMAT.YYYY_MM_DD));

    const [primaryAggregate, ...secondaryAggregates] = get(
      data,
      'dataFields.0.aggregate'
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
   *
   * @returns
   * @memberof WidgetKPIComponent
   */
  prepareDateFilterModel() {
    const model = {
      preset: this.kpiForm.get('filter').value
    };

    if (model.preset !== CUSTOM_DATE_PRESET_VALUE) return model;

    return {
      ...model,
      lte: this.kpiForm.get('lte').value.format(DATE_FORMAT.YYYY_MM_DD),
      gte: this.kpiForm.get('gte').value.format(DATE_FORMAT.YYYY_MM_DD)
    };
  }

  /**
   * Converts the form values to backend-valid structure and notifies parent.
   * Represensts the save/update operation.
   *
   * @memberof WidgetKPIComponent
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
        dataFields: [
          {
            columnName: dataField.columnName,
            name: dataField.name,
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
