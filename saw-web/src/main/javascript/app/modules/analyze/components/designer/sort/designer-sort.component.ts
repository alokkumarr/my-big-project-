import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumns,
  Sort
} from '../types';

const template = require('./designer-sort.component.html');
// require('./designer-sort.component.scss');

@Component({
  selector: 'designer-sort',
  template
})
export class DesignerSortComponent {
  @Input() public artifactColumns: ArtifactColumns;
  @Input() public sorts: Sort[];
}
