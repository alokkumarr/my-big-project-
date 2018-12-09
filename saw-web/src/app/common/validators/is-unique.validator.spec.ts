import { isUnique } from './is-unique.validator';
import { FormControl } from '@angular/forms';
import { of } from 'rxjs';

let formControl1: FormControl;
let formControl2: FormControl;

function checkValidity(formControl: FormControl, value: string, done) {
  const sub = formControl.statusChanges.subscribe(status => {
    if (status === 'VALID') {
      expect(formControl.invalid).toBeFalsy();
      sub.unsubscribe();
      done();
    }
  });

  formControl.setValue(value);
}

function checkInvalidity(formControl: FormControl, value: string, done) {
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
    formControl.setValue(value);
}

describe('Is Unique Validator', () => {

  // return true if duplicate exists, false if the value is unique
  const simpleMockRemoteFunction = (param: string) => of(param === 'duplicate');
  const complexMockRemoteFunction = (param: Object) => of(param === 'complexDuplicate');
  const transformerFn = (value: string) => ({value, someOtherParameter: 'something'});

  beforeEach(() => {
    formControl1 = new FormControl('', [], isUnique(simpleMockRemoteFunction));
    formControl2 = new FormControl('', [], isUnique(complexMockRemoteFunction, transformerFn));
  });

  it('should be valid if control is empty', done => {
    checkValidity(formControl1, '', done);
    checkValidity(formControl2, '', done);
  });

  it('should be invalid if value already exists', done => {
    checkInvalidity(formControl1, 'duplicate', done);
    checkInvalidity(formControl2, 'complexDuplicate', done);
  });

  it('should be valid if value does not exist', done => {
    checkValidity(formControl2, 'whatever', done);
  });
});
