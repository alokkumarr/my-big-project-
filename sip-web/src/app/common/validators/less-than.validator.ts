import { FormGroup, ValidationErrors } from '@angular/forms';
import * as isNumber from 'lodash/isNumber';
/**
 * Form validation: control1 should be less than control2
 *
 * @returns
 */
export const lessThan = (controlName: string, otherControlName: string) => {
  return (thisGroup: FormGroup): ValidationErrors => {
    const value = thisGroup.value;
    const thisValue = value[controlName];
    const otherValue = value[otherControlName];

    if (!isNumber(thisValue) || !isNumber(otherValue)) {
      return null;
    }

    return thisValue < otherValue ? null : { lessThan: true };
  };
};
