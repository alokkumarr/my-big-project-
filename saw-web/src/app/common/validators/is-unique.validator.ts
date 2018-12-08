import { FormControl } from '@angular/forms';
import * as trim from 'lodash/trim';
import { Observable, timer, of } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

const DEBOUNCE_TIME = 500;

export const isUnique = (remoteFn: (param: any) => Observable<boolean>) => {
  return (thisControl: FormControl) => {
    return timer(DEBOUNCE_TIME).pipe(
      switchMap(() => {
        const value = trim(thisControl.value);
        if (!value) {
          return of(null);
        }
        return remoteFn(value).pipe(map(res => res ? null : { isUnique: true }));
      })
    );
  };
};
