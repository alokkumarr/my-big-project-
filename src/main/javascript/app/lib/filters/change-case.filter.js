import camelCase from 'lodash/camelCase';
import kebabCase from 'lodash/kebabCase';
import lowerCase from 'lodash/lowerCase';
import snakeCase from 'lodash/snakeCase';
import startCase from 'lodash/startCase';
import upperCase from 'lodash/upperCase';

export function changeCaseFilter() {
  return (input, caseType) => {
    const modifier = {
      camel: camelCase,
      kebab: kebabCase,
      lower: lowerCase,
      snake: snakeCase,
      title: startCase,
      upper: upperCase
    }[caseType] || (a => a);

    return modifier(input);
  };
}
