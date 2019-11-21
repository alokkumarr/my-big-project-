import { validatePassword, passwordPolicy } from './password-policy.validator';
import { async, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormGroup, FormBuilder } from '@angular/forms';

describe('Password Validator', () => {
  let formGroup: FormGroup;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    const formBuilder: FormBuilder = TestBed.get(FormBuilder);
    formGroup = formBuilder.group({
      password: ['', passwordPolicy()]
    });
  });

  it('should make default form invalid', () => {
    formGroup.updateValueAndValidity();
    expect(formGroup.valid).toEqual(false);
  });

  it('should pass a valid password', () => {
    expect(validatePassword('Password1!').length).toEqual(0);
  });

  it('should fail a password of invalid length', () => {
    expect(validatePassword('Pass1!').length).not.toEqual(0);
  });

  it('should fail if equal to username', () => {
    expect(validatePassword('Password1!', 'Password1!').length).not.toEqual(0);
    expect(validatePassword('Password1!', 'testuser').length).toEqual(0);
  });

  it('should fail if more than one character tests fail', () => {
    /* Should pass for only special character missing */
    expect(validatePassword('Password11').length).toEqual(0);

    /* Should pass for only number missing */
    expect(validatePassword('Password!!').length).toEqual(0);

    /* Should pass for only uppercase missing */
    expect(validatePassword('password1!').length).toEqual(0);

    /* Should pass for only lowercase missing */
    expect(validatePassword('PASSWORD1!').length).toEqual(0);

    /* Should fail for more than one case failing. Here, uppercase and special
    character is missing */
    expect(validatePassword('password11').length).not.toEqual(0);
  });
});
