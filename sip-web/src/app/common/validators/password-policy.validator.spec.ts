import { validatePassword } from './password-policy.validator';

describe('Password Validator', () => {
  it('should pass a valid password', () => {
    expect(validatePassword('Password1!').length).toEqual(0);
  });

  it('should fail a password of invalid length', () => {
    expect(validatePassword('Pass1!').length).not.toEqual(0);
  });

  it('should fail a password without lowercase', () => {
    expect(validatePassword('PPPPPPPPP1!').length).not.toEqual(0);
  });

  it('should fail a password without uppercase', () => {
    expect(validatePassword('ppppppppp1!').length).not.toEqual(0);
  });

  it('should fail a password without number', () => {
    expect(validatePassword('Passwordsss$!').length).not.toEqual(0);
  });

  it('should fail a password without special chars', () => {
    expect(validatePassword('Passwordsss11123').length).not.toEqual(0);
  });
});
