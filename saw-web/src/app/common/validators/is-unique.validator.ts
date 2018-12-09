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

export const isUnique = (isDupicateFn: Request, value2Params: TransformerFn = val => val) => {
  return (thisControl: FormControl) => {
    return timer(DEBOUNCE_TIME).pipe(
      switchMap(() => {
        const value = trim(thisControl.value);
        if (!value) {
          return of(null);
        }
        const errorObject = { isUnique: true };
        const params = value2Params(value);
        return isDupicateFn(params).pipe(map(isDuplicate => isDuplicate ? errorObject : null));
      })
    );
  };
};
