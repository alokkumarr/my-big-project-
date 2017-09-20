import * as template from './binary-option.component.html';
import style from './binary-option.component.scss';

export const BinaryOptionComponent = {
  template,
  styles: [style],
  bindings: {
    ngModel: '<',
    left: '@',
    right: '@',
    onChange: '&'
  },
  controller: class BinaryOptionController {

    $onInit() {
      if (!this.ngModel) {
        this.ngModel = this.left;
      }
    }

    choose(option) {
      // this.ngModel = this[option];
      this.onChange({value: this[option]});
    }
  }
};
