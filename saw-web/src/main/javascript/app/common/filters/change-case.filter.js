import * as camelCase from 'lodash/camelCase';
import * as kebabCase from 'lodash/kebabCase';
import * as lowerCase from 'lodash/lowerCase';
import * as snakeCase from 'lodash/snakeCase';
import * as startCase from 'lodash/startCase';
import * as upperCase from 'lodash/upperCase';

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
