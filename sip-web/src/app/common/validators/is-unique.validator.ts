import { FormControl } from '@angular/forms';
import * as trim from 'lodash/trim';
import { Observable, timer, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const DEBOUNCE_TIME = 500;
/**
 * Parameters used for the isDuplicate request
 */
type RequestParams = any;
type Request = (param: RequestParams) => Observable<boolean>;
/**
 * Function used to transform the value in the form field, to the parameters for the request
 * in case he request need more information then the form field value
 */
type TransformerFn = (value: string) => RequestParams;

/**
 * isUnique valiator checks if the value in the input is unique
 * @param isDuplicateFn function to check if the input value already exists or not
 * @param value2Params function to get the parameters needed for the isDuplicateFn
 *                     if it needs other parameters besides the input value
 * @param oldValue the old value of the input, for example when editing an object, the value that is already
 *                 there should not be checked if it already exists
 */
export const isUnique = (isDupicateFn: Request, value2Params: TransformerFn = val => val, oldValue: string) => {
  return (thisControl: FormControl) => {
    return timer(DEBOUNCE_TIME).pipe(
      switchMap(() => {
        const value = trim(thisControl.value);
        if (!value || (oldValue && oldValue === value)) {
          return of(null);
        }
        const errorObject = { isUnique: true };
        const params = value2Params(value);
        return isDupicateFn(params).pipe(map(isDuplicate => isDuplicate ? errorObject : null));
      })
    );
  };
};
