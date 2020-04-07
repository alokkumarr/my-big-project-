
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Router } from '@angular/router';
import {
  DSKFilterGroup,
  DSKFilterField
} from '../../dsk-filter.model';

import { NUMBER_TYPES, DATE_TYPES } from './../../consts';

import { JwtService } from 'src/app/common/services';
import { DskFiltersService, DskEligibleField } from './../../services/dsk-filters.service';

import * as cloneDeep from 'lodash/cloneDeep';
import * as isEmpty from 'lodash/isEmpty';
import * as groupBy from 'lodash/groupBy';
import * as isUndefined from 'lodash/isUndefined';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';
import * as get from 'lodash/get';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpFilter from 'lodash/fp/filter';

import { Artifact } from './../../../modules/analyze/designer/types';
import { ArtifactDSL } from '../../../models';


const TYPE_MAP = reduce(
  [
    ...map(NUMBER_TYPES, type => ({ type, generalType: 'number' })),
    ...map(DATE_TYPES, type => ({ type, generalType: 'date' })),
    { type: 'string', generalType: 'string' }
  ],
  (typeMap, { type, generalType }) => {
    typeMap[type] = generalType;
    return typeMap;
  },
  {}
);

@Component({
  selector: 'filter-prompt-layout',
  templateUrl: './filter-prompt-layout.component.html',
  styleUrls: ['./filter-prompt-layout.component.scss']
})
export class FilterPromptLayout implements OnInit {
  filterGroup: DSKFilterGroup;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  dskEligibleFields: Array<DskEligibleField> = [];
  @Output() public filterModelChange: EventEmitter<null> = new EventEmitter();
  filteredColumns: [];
  groupedFilters;
  filters;
  public TYPE_MAP = TYPE_MAP;
  @Input() data;
  @Input('filterGroup') set _filterGroup(filters: DSKFilterGroup) {
    this.filterGroup = filters;
    this.onChange.emit(this.filterGroup);
  }
  @Input() selfIndex: number; // stores the position inside parent (for removal)
  @Output() onRemoveGroup = new EventEmitter();
  @Output() onChange = new EventEmitter();
  @Input() showGlobalOption: boolean;

  constructor(
    jwtService: JwtService,
    dskFilterService: DskFiltersService,
    router: Router
  ) {
    if (!(router.url).includes('/analyze/')) {
      dskFilterService
      .getEligibleDSKFieldsFor(jwtService.customerId, jwtService.productId)
      .subscribe(fields => {
        this.dskEligibleFields = fields;
      });
    }
  }

  ngOnInit() {
    this.filters = cloneDeep(this.data.filters);
    this.groupedFilters = groupBy(this.data.filters, 'tableName');
    this.onChange.emit(this.filterGroup);
  }

  fetchColumns(childId) {
    if (isEmpty(get(this.filterGroup.booleanQuery[childId], 'artifactsName'))) {
      return [];
    }
    let dropDownData = this.data.artifacts.find((data: any) =>
      data.artifactName === get(this.filterGroup.booleanQuery[childId], 'artifactsName')
    );
    return dropDownData.columns;
  }

  columnsSelect(column, childId) {
    (<DSKFilterField>this.filterGroup.booleanQuery[childId]).columnName = column;
    const { artifactsName } = (<DSKFilterField>this.filterGroup.booleanQuery[childId]);
    (<DSKFilterField>this.filterGroup.booleanQuery[childId]).type = fpPipe(
      fpFlatMap(artifact => artifact.columns || artifact.fields),
      fpFilter(({ columnName, table }) => {
        return table.toLowerCase === artifactsName.toLowerCase && columnName === column;
      })
    )(this.data.artifacts)[0].type;
    this.onChange.emit(this.filterGroup);
  }

  artifactTrackByFn(_, artifact: Artifact | ArtifactDSL) {
    return (
      (<Artifact>artifact).artifactName || (<ArtifactDSL>artifact).artifactsName
    );
  }

  fetchType(childId) {
    if (isUndefined(this.filterGroup.booleanQuery[childId])) {
      return '';
    }
    return get(this.filterGroup.booleanQuery[childId], 'type');
  }

  onFilterModelChange(filter, childId) {
    (<DSKFilterField>this.filterGroup.booleanQuery[childId]).model = cloneDeep(filter);
    this.onChange.emit(this.filterGroup);
  }

}
