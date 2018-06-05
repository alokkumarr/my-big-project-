import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';
import * as trim from 'lodash/trim';

/**
 * Form validation: Use on fields where input has to be non-blank. i.e.
 * an input which consists of only spaces will fail validation.
 *
 * @returns
 */
export const nonEmpty = () => {
  return (thisControl: FormControl): ValidationErrors => {
    return trim(thisControl.value)
      ? null
      : { nonEmpty: { value: thisControl.value } };
  };
};
