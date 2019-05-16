import { Pipe, PipeTransform } from '@angular/core';
import * as trim from 'lodash/trim';

@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {
  transform(input: string, length: number = 10): string {
    const trimmedInput: string = trim((input || '').toString());
    return trimmedInput.length <= length
      ? trimmedInput
      : `${trimmedInput.substr(0, length)}...`;
  }
}
