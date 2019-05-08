import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';
import * as lowerCase from 'lodash/lowerCase';
import * as includes from 'lodash/includes';

/**
 * An angularjs like filter pipe. Examples:
 * {{someArray | filter:filterValue}}
 *
 * If someArray is an array of objects, and we need
 * to filter matches based on a particular field, we can do:
 *
 * {{someArray | filter:'myField':filterValue}}
 *
 * @export
 * @class FilterPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  transform(input: Array<any>, ...args: Array<any>): Array<any> {
    const field = args.length > 1 ? args[0] : null;
    const value = field ? args[1] : args[0];

    if (!value) {
      return input;
    }

    const compare = (a, b) => typeof a === 'string' || typeof b === 'string' ?
      includes(lowerCase(a), lowerCase(b)) :
      a === b;

    return filter(input, el => field ? compare(el[field], value) : compare(el, value));
  }
}
