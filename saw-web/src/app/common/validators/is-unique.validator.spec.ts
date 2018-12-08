import { isUnique } from './is-unique.validator';
import { FormControl } from '@angular/forms';
import { of } from 'rxjs';

let formControl: FormControl;

describe('Is Unique Validator', () => {

  // only the 'exsts' name exists in the backend, so every other string is unique
  const mockRemoteFunction = param => of(param !== 'exists');


  beforeEach(() => {
    formControl = new FormControl('', [], isUnique(mockRemoteFunction));
  });

  it('should be valid if control is empty', done => {
    const sub = formControl.statusChanges.subscribe(status => {
      if (status === 'VALID') {
        expect(formControl.invalid).toBeFalsy();
        sub.unsubscribe();
        done();
      }
    });
    formControl.setValue('');
  });

  it('should be invalid if value already exists', done => {
    const expectedObj = { isUnique: true };

    const sub = formControl.statusChanges.subscribe(status => {
      if (status === 'INVALID') {
        expect(formControl.invalid).toBeTruthy();
        expect(formControl.errors).toEqual(
          jasmine.objectContaining(expectedObj)
        );
        sub.unsubscribe();
        done();
      }
    });

    formControl.setValue('exists');
  });

  it('should be valid if value does not exist', done => {
    const sub = formControl.statusChanges.subscribe(status => {
      if (status === 'VALID') {
        expect(formControl.invalid).toBeFalsy();
        sub.unsubscribe();
        done();
      }
    });
    formControl.setValue('whatever');
  });
});
