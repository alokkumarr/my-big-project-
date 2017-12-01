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
  pure: true
})
export class ArtifactColumnFilterPipe implements PipeTransform {
  transform(items: ArtifactColumns, filterObj: ArtifactColumnFilter): any {
    if (!items || !filter) {
      return items;
    }

    return filter(items, ({type, columnName}) => {
      return this.hasType(type, filterObj) &&
        this.hasKeyword(columnName, filterObj)
    });
  }

  hasType(type, filterObj) {
    switch (filterObj.type) {
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

  hasKeyword(columnName, {keyword}) {
    if (!keyword) {
      return true;
    }
    return columnName.includes(keyword);
  }
}
