import { Pipe, PipeTransform } from '@angular/core';
import * as round from 'lodash/round';

@Pipe({
  name: 'round'
})

export class RoundPipe implements PipeTransform {
  transform(value: any, precision: number): any {
    if (precision) {
      return round(value, precision);
    }
    return round(value);
  }
}
