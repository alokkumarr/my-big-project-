import { requireIf } from './required-if.validator';
import { FormGroup, FormBuilder, ReactiveFormsModule } from '@angular/forms';

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
});
