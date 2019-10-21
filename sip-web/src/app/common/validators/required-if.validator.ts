import { FormControl, ValidationErrors } from '@angular/forms';

/**
 * Form validation: Used when a field needs to be required depending on
 * valud of another field.
 *
 * Example: The date fields are required only if preset is set to custom
 *
 * @param {string} otherField
 * @param {(any) => boolean} fn
 * @returns
 */
export const requireIf = (
  otherField: string | ((FormControl) => FormControl),
  fn: (any) => boolean
) => {
  return (thisControl: FormControl): ValidationErrors => {
    if (!thisControl.parent) {
      return null;
    }

    let otherControl: FormControl;

    if (typeof otherField === 'string') {
      otherControl = thisControl.parent.get(otherField) as FormControl;
    } else {
      otherControl = otherField(otherControl);
    }

    if (!otherControl) {
      throw new Error(`${otherField} not found in the form.`);
    }

    if (!fn(otherControl.value)) {
      return null;
    }

    return thisControl.value
      ? null
      : { required: { value: thisControl.value } };
  };
};
