import templateUrl from './range-slider.component.html';

/**
 * @example usage in an html example:
 * <range-slider
 *   lower-value="1"
 *   upper-value="9"
 *   min-gap="1"
 *   step="1"
 *   min="0"
 *   max="10" >
 * </range-slider>
 *
 * the input constraints are:
 * min < max - step
 * max > min + step
 * min-gap > 0
 * step > 0
 * min-gap >= step
 * lower-value >= min
 * upper-value <= max
 * lower-value < upper-value - min-gap
 * upper-value > lower-value + min-gap
 *
 * @class RangeSliderController
 */
export const RangeSliderComponent = {
  bindings: {
    max: '=',
    min: '=',
    minGap: '=',
    step: '=',
    lowerValue: '=lowerValue',
    upperValue: '=upperValue'
  },
  template: templateUrl,
  controller: class RangeSliderController {
    constructor($scope) {
      'ngInject';

      this.scope = $scope;
    }

    $onInit() {
      // TODO: add errors for input constraints
      if (!this.step) {
        this.step = 1;
      }
      this.scope.$watchGroup(() => [this.min, this.max], this.minMaxWatcher.bind(this));
      this.scope.$watch(() => this.lowerValue, this.lowerValueWatcher.bind(this));
    }

    minMaxWatcher() {
      this.lowerMax = this.max - this.step;
      this.upperMin = this.lowerValue + this.step;

      if (!this.lowerValue || this.lowerValue < this.min) {
        this.lowerValue = this.min;
      } else {
        this.lowerValue *= 1;
      }
      if (!this.upperValue || this.upperValue > this.max) {
        this.upperValue = this.max;
      } else {
        this.upperValue *= 1;
      }
      this.updateWidth();
    }

    lowerValueWatcher() {
      const leftSliderUpperBoundary = this.upperValue - this.step;
      if (this.lowerValue >= leftSliderUpperBoundary) {
        this.lowerValue = leftSliderUpperBoundary;
        return;
      }
      this.upperMin = this.lowerValue + this.step;

      this.updateWidth();
    }

    updateWidth() {
      this.upperWidth = `${(((this.max - (this.lowerValue + this.step)) / (this.max - this.min)) * 100)}%`;
      if (this.lowerValue > (this.upperValue - this.minGap) && this.upperValue < this.max) {
        this.upperValue = this.lowerValue + this.minGap;
      }
    }
  }
};
