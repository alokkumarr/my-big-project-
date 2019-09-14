import { FormControl, ValidationErrors } from '@angular/forms';
import * as isNumber from 'lodash/isNumber';
/**
 * Form validation: control1 should be less than control2
 *
 * @returns
 */
export const lessThan = (otherControl: string) => {
  return (thisControl: FormControl): ValidationErrors => {
    if (!thisControl.parent) {
      return null;
    }
    const thisValue = thisControl.value;
    const otherValue = thisControl.parent.value[otherControl];

    if (!isNumber(thisValue) || !isNumber(otherValue)) {
      return null;
    }

    return thisValue < otherValue ? null : { lessThan: { value: true } };
  };
};
