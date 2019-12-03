import { validateEntityName, entityNameErrorMessage } from './field-name-rule.validator';

describe('entity name validation', () => {

  it('validation for analysis name should fail if name has a * special character', () => {
    const checkAnalysisNameChar = validateEntityName('ABC*');
    // should fail for usage of special characters
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toBeTruthy();
  });

  it('should fail for length validation', () => {
    const checkAnalysisNameChar = validateEntityName(
      'QWERTYUIOPSDFGHJKLZXCVBNM234567890QWERTYUIOPASDFGHJKL'
    );
    // should fail for length validation
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toBeTruthy();
  });

  it('should display correct error message to user', () => {
    const checkAnalysisNameChar = entityNameErrorMessage(
      'nameLength'
    );
    // should fail for length validation
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toEqual(
      '* Dashboard Name cannot be empty or exceed 30 characters.'
    );
  });

  it('should display correct error message to user', () => {
    const checkAnalysisNameChar = entityNameErrorMessage(
      'specialChars'
    );
    // should fail for length validation
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toEqual(
      '* Dashboard Name cannot contain special characters.'
    );
  });
});

