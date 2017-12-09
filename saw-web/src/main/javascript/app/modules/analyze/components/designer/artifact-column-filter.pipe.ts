import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';

import {
  ArtifactColumns,
  ArtifactColumnFilter
} from './types';
import {
  NUMBER_TYPES,
  DATE_TYPES
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
      return this.hasType(type, filterObj.type) &&
        this.hasKeyword(alias || displayName, filterObj.keyword)
    });
  }

  hasType(type, filterType) {
    switch (filterType) {
    case 'number':
      return NUMBER_TYPES.includes(type);
    case 'date':
      return DATE_TYPES.includes(type);
    case 'string':
      return type === 'string';
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
