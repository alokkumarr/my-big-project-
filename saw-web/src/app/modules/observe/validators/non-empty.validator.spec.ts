import { nonEmpty } from './non-empty.validator';
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

describe('Non Empty Validator', () => {
  let form: FormGroup;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [],
      providers: []
    });
    const fb: FormBuilder = TestBed.get(FormBuilder);
    form = fb.group({
      field1: ['', nonEmpty()]
    });
  });

  it('should be invalid if control empty', () => {
    form.get('field1').setValue('');

    expect(form.invalid).to.be.true;
    expect(form.get('field1').errors).to.have.property('nonEmpty');
  });

  it('should be valid if control empty', () => {
    form.get('field1').setValue('      a     b');

    expect(form.invalid).to.be.false;
  });

  it('should be invalid if control only has spaces', () => {
    form.get('field1').setValue('         ');
    expect(form.invalid).to.be.true;
    expect(form.get('field1').errors).to.have.property('nonEmpty');
  });
});
