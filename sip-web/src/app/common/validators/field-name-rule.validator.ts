const minimumNameLength = 30;

const checkEntityName = (name) => {
  const analysisNameLength = name.length;
  // Due to an error in generating an excel file during dispatch opearation,
  // we need to apply the following length and special character rules.
  const validateCheck = {
    validateLength: analysisNameLength === 0 || analysisNameLength > minimumNameLength ? true : false,
    validateCharacters: /[`~!@#$%^&*()+={}|"':;?/>.<,*:/?[\]\\]/g.test(name)
  };
  const { validateLength, validateCharacters } = validateCheck;
  const validationStateFail = validateLength || validateCharacters;
  return {validationStateFail, validateCheck};
};

export const validateEntityName = (analysisName)  => {
  const validateState = checkEntityName(analysisName);
  return {
    state: validateState.validationStateFail,
    check: validateState.validateCheck
  };
};

export const entityNameErrorMessage = (failState) => {
  switch (failState) {
    case 'nameLength':
      return `* Dashboard Name cannot be empty or exceed ${minimumNameLength} characters.`;
    case 'specialChars':
      return `* Dashboard Name cannot contain special characters.`;
  }
};
