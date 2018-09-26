import { FormControl, FormGroup, ValidationErrors } from '@angular/forms';
import { Subscription } from 'rxjs/Subscription';

/**
 * Form validation: Used when a field needs to be required depending on
 * valud of another field.
 *
 * Example: The date fields are required only if preset is set to custom
 *
 * @param {string} otherFieldName
 * @param {(any) => boolean} fn
 * @returns
 */
export const requireIf = (otherFieldName: string, fn: (any) => boolean) => {
  return (thisControl: FormControl): ValidationErrors => {
    if (!thisControl.parent) {
      return null;
    }

    const otherControl = thisControl.parent.get(otherFieldName) as FormControl;

    if (!otherControl)
      throw new Error(`${otherFieldName} not found in the form.`);

    if (!fn(otherControl.value)) return null;

    return thisControl.value
      ? null
      : { required: { value: thisControl.value } };
  };
};
