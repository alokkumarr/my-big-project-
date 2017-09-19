import * as map from 'lodash/map';
import * as isEmpty from 'lodash/isEmpty';

import * as template from './checkbox-filter.component.html';

export const CheckboxFilterComponent = {
  template,
  require: {
    ngModelController: 'ngModel'
  },
  bindings: {
    filter: '<',
    ngModel: '<'
  },
  controller: class CheckboxFilterController {

    $onChanges() {
      if (!this.ngModel || isEmpty(this.ngModel)) {
        this.ngModel = map(this.filter.items, item => {
          return {
            label: item,
            checked: false
          };
        });
      }
    }

    onToggle(item) {
      item.checked = !item.checked;
      this.ngModelController.$setViewValue(this.ngModel);
    }
  }
};
