import {
  Component,
  ChangeDetectionStrategy,
  ViewChild,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { DxRangeSliderComponent } from 'devextreme-angular/ui/range-slider';
import { ObserveService } from '../../../services/observe.service';
import {
  GlobalFilterService,
  isValidNumberFilter as isValid
} from '../../../services/global-filter.service';

import { Subscription } from 'rxjs';
import * as round from 'lodash/round';
import * as get from 'lodash/get';

@Component({
  selector: 'g-number-filter',
  templateUrl: 'number-filter.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class GlobalNumberFilterComponent implements OnInit, OnDestroy {
  @Output() onModelChange = new EventEmitter();
  @ViewChild('slider', { static: true }) slider: DxRangeSliderComponent;

  public _filter;
  public step = 1; // tslint:disable-line
  public defaults: { min; max } = { min: 1, max: 100 };
  public filterCache: { operator?; start?; end? };
  public value: Array<number>;
  public clearFiltersListener: Subscription;
  public applyFiltersListener: Subscription;
  public closeFiltersListener: Subscription;
  public sliderTooltip = {
    enabled: true,
    format: (value: number) => round(value, 2),
    showMode: 'always',
    position: 'top'
  };
  private defaultsLoaded = false;

  constructor(
    public observe: ObserveService,
    public filters: GlobalFilterService
  ) {}

  ngOnInit() {
    this.clearFiltersListener = this.filters.onClearAllFilters.subscribe(() => {
      this.loadDefaults();
      this.cacheFilters();
    });

    this.applyFiltersListener = this.filters.onApplyFilter.subscribe(() => {
      this.cacheFilters();
    });

    this.closeFiltersListener = this.filters.onSidenavStateChange.subscribe(
      state => {
        if (!state) {
          this.loadDefaults(true); // load cached filter data since last apply
        } else {
          /* When slider is painted in display: none component, tooltips look incorrect.
          Need to call repaint when container sidenav is opened to recalculate correct
          styles */
          this.slider.instance.repaint();
        }
      }
    );
  }

  ngOnDestroy() {
    this.clearFiltersListener.unsubscribe();
    this.applyFiltersListener.unsubscribe();
    this.closeFiltersListener.unsubscribe();
  }

  @Input()
  set filter(data) {
    this._filter = data;

    if (data.model) {
      this.value = [data.model.otherValue, data.model.value];
      this.cacheFilters();
      this.loadMinMax(true);
    } else {
      this.value = [this.defaults.min, this.defaults.max];
      this.loadMinMax(false);
    }
  }

  /**
   * Loads minimum and maximum values for this number field from backend.
   *
   * @memberof GlobalNumberFilterComponent
   */
  loadMinMax(useCache: boolean = false) {
    this.observe.getModelValues(this._filter).subscribe(data => {
      this.defaults.min = parseFloat(get(data, `_min`, this.defaults.min));
      this.defaults.max = parseFloat(get(data, `_max`, this.defaults.max));

      /* Give time for changes to min/max to propagate properly. The
        range slider may use a settimeout to update changes in min/max.
      */
      this.defaultsLoaded = true;
      setTimeout(() => {
        this.loadDefaults(useCache);
        this.cacheFilters();
      }, 10);
    });
  }

  /**
   * Caches filter in-memory. Cache is used to revert to last
   * applied / default values.
   *
   * @memberof GlobalNumberFilterComponent
   */
  cacheFilters() {
    this.filterCache = {
      operator: 'BTW',
      start: this.value[0],
      end: this.value[1]
    };
  }

  /**
   * Resets the slider start and end to default state or
   * last cached state as needed.
   *
   * @param {boolean} [fromCache=false]
   * @returns
   * @memberof GlobalNumberFilterComponent
   */
  loadDefaults(fromCache = false) {
    if (fromCache && !this.filterCache) {
      return;
    }

    /* prettier-ignore */
    const loadData = fromCache
      ? this.filterCache
      : {
        start: this.defaults.min,
        end: this.defaults.max
      };

    this.onSliderChange([loadData.start, loadData.end]);
  }

  /**
   * Gets the filter model together and communicates the
   * updated filter to the parent.
   *
   * @param {any} data
   * @memberof GlobalNumberFilterComponent
   */
  onSliderChange(data) {
    if (!this.defaultsLoaded) {
      return;
    }
    this.value = data;
    const payload = {
      ...this._filter,
      ...{
        model: {
          value: data[1],
          otherValue: data[0],
          operator: 'BTW'
        }
      }
    };

    this.onModelChange.emit({ data: payload, valid: isValid(payload) });
  }
}
