import { FormGroup, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { lessThan } from './less-than.validator';
import { async, TestBed } from '@angular/core/testing';

describe('Less than validator', () => {
  let formGroup: FormGroup;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    const formBuilder: FormBuilder = TestBed.get(FormBuilder);
    formGroup = formBuilder.group(
      {
        control1: [1],
        control2: [2]
      },
      { validators: [lessThan('control1', 'control2')] }
    );
    formGroup.updateValueAndValidity();
  });

  it('should be valid', () => {
    expect(formGroup.valid).toEqual(true);
  });

  it('should be invalid if values are reversed', () => {
    formGroup.get('control1').setValue(2);
    formGroup.get('control2').setValue(1);
    formGroup.updateValueAndValidity();
    expect(formGroup.valid).toEqual(false);
  });
});
