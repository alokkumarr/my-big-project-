import { Pipe, PipeTransform } from '@angular/core';
import * as camelCase from 'lodash/camelCase';
import * as kebabCase from 'lodash/kebabCase';
import * as lowerCase from 'lodash/lowerCase';
import * as snakeCase from 'lodash/snakeCase';
import * as startCase from 'lodash/startCase';
import * as upperCase from 'lodash/upperCase';

type CaseType = 'camel' | 'kebab' | 'lower' | 'snake' | 'title' | 'upper';

/**
 * A ChangeCasePipe pipe, to change the case of text Examples:
 * {{someText | changeCase:changeCaseValue}}
 *
 * @export
 * @class HighlightPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'changeCase'
})
export class ChangeCasePipe implements PipeTransform {

  transform(input: string, caseType: CaseType): string {

    const modifier = {
      camel: camelCase,
      kebab: kebabCase,
      lower: lowerCase,
      snake: snakeCase,
      title: startCase,
      upper: upperCase
    }[caseType] || (a => a);

    return modifier(input);
  }
}
