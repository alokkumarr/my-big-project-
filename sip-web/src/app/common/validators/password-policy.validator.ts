import { FormControl, ValidationErrors } from '@angular/forms';
import * as fpPipe from 'lodash/fp/pipe';
import * as curry from 'lodash/curry';
import * as trim from 'lodash/trim';

export enum PasswordError {
  shortLength,
  lowercaseMissing,
  uppercaseMissing,
  numberMissing,
  specialCharMissing
}

const requiredMinimumLength = 8;
const specialCharsAllowed = '~!@#$%^&*?<>';

type PasswordValidator = (
  password: string,
  errors: PasswordError[]
) => PasswordError[];

const passwordErrorToMessage = (error: PasswordError): string => {
  switch (error) {
    case PasswordError.shortLength:
      return `Password should have at least ${requiredMinimumLength} characters.`;
    case PasswordError.lowercaseMissing:
      return 'Password should have a lower case letter.';
    case PasswordError.uppercaseMissing:
      return 'Password should have an upper case letter.';
    case PasswordError.numberMissing:
      return 'Password should have a number.';
    case PasswordError.specialCharMissing:
      return `Password should have a special character (${specialCharsAllowed}).`;
  }
};

const validateLength: PasswordValidator = (password, errors) => {
  return trim(password).length >= 8
    ? errors
    : [...errors, PasswordError.shortLength];
};

const validateLowercase: PasswordValidator = (password, errors) => {
  return /[a-z]/.test(password)
    ? errors
    : [...errors, PasswordError.lowercaseMissing];
};

const validateUppercase: PasswordValidator = (password, errors) => {
  return /[A-Z]/.test(password)
    ? errors
    : [...errors, PasswordError.uppercaseMissing];
};

const validateNumber: PasswordValidator = (password, errors) => {
  return /[0-9]/.test(password)
    ? errors
    : [...errors, PasswordError.numberMissing];
};

const validateSpecialChar: PasswordValidator = (password, errors) => {
  return RegExp(`[${specialCharsAllowed}]`).test(password)
    ? errors
    : [...errors, PasswordError.specialCharMissing];
};

export const validatePassword = (password: string): string => {
  const errors = fpPipe(
    curry(validateLength)(password),
    curry(validateLowercase)(password),
    curry(validateUppercase)(password),
    curry(validateNumber)(password),
    curry(validateSpecialChar)(password)
  )([]);
  return errors.map(passwordErrorToMessage).join('\n');
};

/**
 * Form validation: Use on password fields to enforce
 * synchronoss standard password policy.
 *
 * @returns
 */
export const passwordPolicy = () => {
  return (thisControl: FormControl): ValidationErrors => {
    const errors = validatePassword(thisControl.value);
    return errors.length === 0
      ? null
      : { passwordPolicy: { value: thisControl.value, errors } };
  };
};
