import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumns,
  ArtifactColumn,
  Filter
} from '../types';
import { TYPE_MAP } from '../../../consts';

const template = require('./designer-filter.component.html');
// require('./designer-filter.component.scss');

@Component({
  selector: 'designer-filter',
  template
})
export class DesignerFilterComponent {
  @Output() public filtersChange: EventEmitter<Filter[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;
  @Input() public filters: Filter[];

  public nameMap: Object = {};
  public TYPE_MAP = TYPE_MAP;
}
