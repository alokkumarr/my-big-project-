import { requireIf } from './required-if.validator';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  ReactiveFormsModule
} from '@angular/forms';

import { expect } from 'chai';

import { configureTests } from '../../../../../../test/javascript/helpers/configureTests';
import { TestBed, inject } from '@angular/core/testing';

configureTests();

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

    expect(form.invalid).to.be.false;
  });

  it('should validate if condition true', () => {
    form.get('field1').setValue('abc');
    form.get('field2').setValue('');

    expect(form.invalid).to.be.true;
    expect(form.get('field2').errors).to.have.property('required');
  });

  it('should update validity correctly', () => {
    form.get('field1').setValue('abc');
    form.get('field2').setValue('');
    expect(form.invalid).to.be.true;

    form.get('field1').setValue('');
    form.get('field2').updateValueAndValidity();
    expect(form.invalid).to.be.false;
  });
});
