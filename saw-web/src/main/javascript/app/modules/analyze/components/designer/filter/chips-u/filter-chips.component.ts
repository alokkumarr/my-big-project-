import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as reduce from 'lodash/reduce';
import {
  Filter,
  Artifact,
  ArtifactColumn
} from '../../types';

const template = require('./filter-chips.component.html');
require('./filter-chips.component.scss');

@Component({
  selector: 'filter-chips-u',
  template,
  styles: [`
    :host {
      display: block;
    }
  `]
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number>= new EventEmitter();
  @Output() removeAll: EventEmitter<null>= new EventEmitter();
  @Input() filters: Filter[];
  @Input('artifacts') set artifacts(artifacts: Artifact[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = reduce(artifacts, (acc, artifact: Artifact) => {
      acc[artifact.artifactName] = reduce(artifact.columns, (acc, col: ArtifactColumn) => {
        acc[col.columnName] = col.displayName;
        return acc;
      }, {});
      return acc;
    }, {});
  };
  @Input() readonly: boolean;

  public nameMap;

  getDisplayName(filter) {
    return filter.column ?
      filter.column.alias || filter.column.displayName :
      this.nameMap[filter.tableName][filter.columnName];
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
