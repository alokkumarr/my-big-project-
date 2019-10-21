import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';

/**
 * An angularjs like filter pipe. Examples:
 *
 * {{someArray | filterFunction}}
 *
 * @export
 * @class FilterPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'filterFn'
})
export class FilterFnPipe implements PipeTransform {
  transform(input: Array<any>, filterFn: (value: any) => boolean): Array<any> {
    if (!filterFn || !input) {
      return input;
    }

    return filter(input, filterFn);
  }
}
