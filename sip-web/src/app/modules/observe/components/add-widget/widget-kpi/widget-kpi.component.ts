import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  FormGroup,
  FormBuilder,
  Validators,
  ValidatorFn
} from '@angular/forms';

import { MatDialog, MatDialogConfig } from '@angular/material';
import {
  DesignerFilterDialogComponent
} from './../../../../analyze/designer/filter';

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
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as isEmpty from 'lodash/isEmpty';

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
  userOptedFilters: any;
  creteriaType: boolean;

  @Output() onKPIAction = new EventEmitter();

  dateFilters = DATE_PRESETS;
  showDateFields = false;
  aggregations = KPI_AGGREGATIONS;
  kpiBgColors = KPI_BG_COLORS;

  chartTypes = ['bullet', 'gauge'];

  kpiForm: FormGroup;
  datePresetSubscription: Subscription;
  primaryAggregationSubscription: Subscription;

  constructor(
    private fb: FormBuilder,
    public dialog: MatDialog
  ) {}

  ngOnInit() {
    this.createForm();
  }

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

    const getRangeValidator = (errorProp: string, from: string, to: string) => (
      fg: FormGroup
    ) => {
      const start = fg.get(from).value;
      const end = fg.get(to).value;
      return start !== null && end !== null && start < end
        ? null
        : { [errorProp]: true };
    };

    const isBulletKpi = this._kpiType === 'bullet';

    const measureRangeValidator: ValidatorFn = getRangeValidator(
      'measureRange',
      'measure1',
      'measure2'
    );
    const targetRangeValidator: ValidatorFn = getRangeValidator(
      'targetRange',
      'measure2',
      'target'
    );

    const bulletValidators = isBulletKpi
      ? { validators: [measureRangeValidator, targetRangeValidator] }
      : {};
    const additionalFields = isBulletKpi
      ? {
          target: [1, [Validators.required, nonEmpty(), Validators.min(1)]],
          kpiDisplay: [this.chartTypes[0]],
          measure1: ['', Validators.required],
          measure2: ['', Validators.required]
        }
      : {};

    this.kpiForm = this.fb.group(
      {
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
        ...additionalFields,
        kpiBgColor: ['blue', Validators.required]
      },
      bulletValidators
    );

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

    setTimeout(() => {
      this.kpiForm
        .get('dateField')
        .setValue(kpiDateField || data.dateColumns[0].columnName);
    });
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
    this.userOptedFilters = fpPipe(
      fpFilter(({ primaryKpiFilter }) => {
        return !primaryKpiFilter;
      })
    )(this._kpi.filters);
    // cover backward compatibility
    if (this._kpi.filters.length === 1) {
      this._kpi.filters[0].primaryKpiFilter = true;
    }

    setTimeout(() => {
      if (data.kpiDisplay) {
        this.kpiForm.get('kpiDisplay').setValue(data.kpiDisplay);
      }

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

      let filt = get(data, 'filters.0.model.preset');
      let dateField = get(data, 'filters.0.columnName');
      if (isUndefined(filt)) {
        forEach(data.filters, primaryfilter => {
          if (primaryfilter.primaryKpiFilter) {
            filt = primaryfilter.model.preset;
            dateField = primaryfilter.model.columnName;
          }
        });
      }
      dateField && this.kpiForm.get('dateField').setValue(dateField);
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

    const additionalFields =
      this._kpiType === 'bullet'
        ? {
            kpiDisplay: this.kpiForm.get('kpiDisplay').value,
            target: toNumber(this.kpiForm.get('target').value),
            measure1: toNumber(this.kpiForm.get('measure1').value),
            measure2: toNumber(this.kpiForm.get('measure2').value)
          }
        : {};
    const defaultFilter = {
      type: dateField.type,
      columnName: dateField.columnName,
      model: this.prepareDateFilterModel(),
      primaryKpiFilter: true
    };
    this.onKPIAction.emit({
      kpi: assign({}, this._kpi, {
        name: this.kpiForm.get('name').value,
        ...additionalFields,
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
        booleanCriteria: this.creteriaType,
        filters: this.constructRequestParamsFilters(defaultFilter)
      })
    });
  }

  constructRequestParamsFilters(defaultFilter) {
    if (isEmpty(this.userOptedFilters) || isUndefined(this.userOptedFilters)) {
      return [defaultFilter];
    }
    const index = this.userOptedFilters.findIndex(x => x.columnName === defaultFilter.columnName);
    if (index === -1) {
      this.userOptedFilters.push(defaultFilter);
    }
    return this.userOptedFilters;
  }

  filterSelectedFilter() {
    const dateField = find(
      this._metric.dateColumns,
      col => col.columnName === this.kpiForm.get('dateField').value
    );
    let primaryFilter;
    return fpPipe(
      fpFlatMap(artifact => artifact.columns),
      fpFilter(({ columnName }) => {
        forEach(this._kpi.filters, filtr => {
          if (filtr.primaryKpiFilter) {
            primaryFilter = filtr;
          }
        });
        return isEmpty(primaryFilter) ? columnName !== dateField.columnName : columnName !== primaryFilter.columnName;
      })
    )(this._metric.artifacts);
  }

  onfilterAction() {
    const artifacts = [{
      artifactName: this._metric.artifacts[0].artifactName,
      columns: this.filterSelectedFilter()
    }];
    const filters = this.userOptedFilters;
    this.openFilterDialog(filters, artifacts, this._kpi.booleanCriteria || 'AND')
    .afterClosed().subscribe((result) => {
      if (result) {
        this.userOptedFilters = result.filters;
        this.creteriaType = result.booleanCriteria;
      }
    });
  }

  openFilterDialog(
    filters,
    artifacts,
    booleanCriteria
  ) {
    const data = {
      filters,
      artifacts,
      booleanCriteria,
      supportsGlobalFilters: false,
      isInRuntimeMode: false,
      showFilterOptions: false
    };
    return this.dialog.open(DesignerFilterDialogComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data
    } as MatDialogConfig);
  }
}
