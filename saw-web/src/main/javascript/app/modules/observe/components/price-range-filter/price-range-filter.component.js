import * as isUndefined from 'lodash/isUndefined';
import * as template from './price-range-filter.component.html';

const MAX_PRICE = 100;
const MIN_PRICE = 0;
export const PriceRangeFilterComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    filter: '<',
    ngModel: '<'
  },
  controller: class PriceRangeFilterController {
    constructor($scope) {
      'ngInject';
      this.$scope = $scope;
      this.rangeSlider = {
        min: MIN_PRICE,
        max: MAX_PRICE,
        minGap: 1,
        step: 1
      };
    }

    $onInit() {
      this.$scope.$watchGroup([
        () => isUndefined(this.ngModel) ? this.ngModel : this.ngModel.fromPrice,
        () => isUndefined(this.ngModel) ? this.ngModel : this.ngModel.toPrice
      ], range => {
        if (isUndefined(range[0]) && isUndefined(range[1])) {
          this.ngModelController.$setViewValue(undefined);
        } else if (range[0] === MIN_PRICE && range[1] === MAX_PRICE) {
          this.ngModelController.$setViewValue(undefined);
        } else {
          this.ngModelController.$setViewValue({
            fromPrice: range[0],
            toPrice: range[1]
          });
        }
      });
    }

    $onChanges() {
      if (!this.ngModel) {
        this.ngModel = {
          fromPrice: MIN_PRICE,
          toPrice: MAX_PRICE
        };
      }
    }
  }
};
