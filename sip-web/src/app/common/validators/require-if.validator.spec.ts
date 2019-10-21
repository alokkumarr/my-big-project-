import { requireIf } from './required-if.validator';
import {
  FormGroup,
  FormBuilder,
  ReactiveFormsModule,
  FormControl
} from '@angular/forms';

import { TestBed } from '@angular/core/testing';

describe('Required if Validator', () => {
  let form: FormGroup;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [],
      providers: []
    });
    const fb: FormBuilder = TestBed.get(FormBuilder);
    form = fb.group({
      field1: [''],
      field2: ['', [requireIf('field1', val => Boolean(val))]]
    });
  });

  it('should not validate if condition false', () => {
    form.get('field1').setValue('');
    form.get('field2').updateValueAndValidity();

    expect(form.invalid).toBeFalsy();
  });

  it('should validate if condition true', () => {
    const expectedObj = { required: { value: '' } };
    form.get('field1').setValue('abc');
    form.get('field2').setValue('');
    expect(form.invalid).toBeTruthy();
    expect(form.get('field2').errors).toEqual(
      jasmine.objectContaining(expectedObj)
    );
  });

  it('should update validity correctly', () => {
    form.get('field1').setValue('abc');
    form.get('field2').setValue('');
    expect(form.invalid).toBeTruthy();

    form.get('field1').setValue('');
    form.get('field2').updateValueAndValidity();
    expect(form.invalid).toBeFalsy();
  });

  it('should work if supplied with a function that returns form control', () => {
    const fb: FormBuilder = TestBed.get(FormBuilder);
    const localForm = fb.group({
      field1: [''],
      field2: [
        '',
        [
          requireIf(
            control => localForm.get('field1') as FormControl,
            val => Boolean(val)
          )
        ]
      ]
    });
    const expectedObj = { required: { value: '' } };
    localForm.get('field1').setValue('abc');
    localForm.get('field2').setValue('');
    expect(localForm.invalid).toBeTruthy();
    expect(localForm.get('field2').errors).toEqual(
      jasmine.objectContaining(expectedObj)
    );
  });
});
