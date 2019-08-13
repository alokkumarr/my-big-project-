import { FormControl, ValidationErrors } from '@angular/forms';
import { correctTimeInterval } from './time-interval-parser';

/**
 * Form validation: Use on fields where input has to be non-blank. i.e.
 * an input which consists of only spaces will fail validation.
 *
 * @returns
 */
export const timeIntervalValidator = () => {
  return (thisControl: FormControl): ValidationErrors => {
    const correctedValue = correctTimeInterval(thisControl.value);
    thisControl.setValue(correctedValue);
    return null;
  };
};
