import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';

import {
  ArtifactColumns,
  ArtifactColumnFilter
} from './types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  TYPE_MAP
} from '../../consts'

@Pipe({
  name: 'artifactColumnFilter',
  pure: false
})
export class ArtifactColumnFilterPipe implements PipeTransform {
  transform(items: ArtifactColumns, filterObj: ArtifactColumnFilter): any {
    if (!items || !filter) {
      return items;
    }

    return filter(items, ({type, alias, displayName}) => {
      return this.hasType(type, filterObj.types) &&
        this.hasKeyword(alias || displayName, filterObj.keyword)
    });
  }

  hasType(type, filterTypes) {

    switch (TYPE_MAP[type]) {
    case 'number':
      return filterTypes.includes('number');
    case 'date':
      return filterTypes.includes('date');
    case 'string':
      return filterTypes.includes('string');
    default:
      return true;
    }
  }

  hasKeyword(name, keyword) {
    if (!keyword) {
      return true;
    }
    const regexp = new RegExp(keyword, 'i');
    return name && name.match(regexp);
  }
}
