declare const require: any;

import {
  Component,
  Input,
  Output,
  EventEmitter,
  ViewContainerRef,
  ViewChild
} from '@angular/core';
import * as find from 'lodash/find';
import * as unset from 'lodash/unset';
import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {
  ArtifactColumn,
  Filter,
  FilterModel
} from '../../types';
import { TYPE_MAP } from '../../../../consts';

const template = require('./designer-filter-row.component.html');
require('./designer-filter-row.component.scss');

@Component({
  selector: 'designer-filter-row',
  template
})
export class DesignerFilterRowComponent {
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Output() public filterChange: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumn[];
  @Input() public filter: Filter;

  @ViewChild('auto', {read: ViewContainerRef}) _autoComplete: ViewContainerRef;

  public TYPE_MAP = TYPE_MAP;
  formControl: FormControl = new FormControl();
  filteredColumns: Observable<ArtifactColumn[]>;

  constructor() {
    this.displayWith = this.displayWith.bind(this);
  }

  ngOnInit() {
    const target = find(this.artifactColumns, ({columnName}) => this.filter.columnName);
    this.formControl.setValue(target);
    this.filteredColumns = this.formControl.valueChanges
      .pipe(
        startWith<string | ArtifactColumn>(''),
        map(value => typeof value === 'string' ? value : value.aliasName || value.displayName),
        map(name => name ? this.nameFilter(name) : this.artifactColumns.slice())
      );
  }

  clearInput() {
    this.formControl.setValue('');
    unset(this.filter, 'columnName');
    unset(this.filter, 'type');
    unset(this.filter, 'model');
    unset(this.filter, 'isRuntimeFilter');
    this.filterChange.emit();
  }

  nameFilter(name: string): ArtifactColumn[] {
    return this.artifactColumns.filter(option => {
      const optionName = option.aliasName || option.displayName;
      return optionName.toLowerCase().indexOf(name.toLowerCase()) > -1;
    });
  }

  onArtifactColumnSelected(columnName) {
    const target: ArtifactColumn = find(this.artifactColumns, column => column.columnName === columnName);
    this.filter.columnName = target.columnName;
    this.filter.type = target.type;
    if (this.filter.isRuntimeFilter) {
      delete this.filter.model;
    } else {
      this.filter.model = null;
    }
    this.filterChange.emit();
  }

  onFilterModelChange(filterModel: FilterModel) {
    this.filter.model = filterModel;
  }

  onRuntimeCheckboxToggle(filter: Filter, checked: boolean) {
    filter.isRuntimeFilter = checked;
    delete filter.model;
  }

  remove() {
    this.removeRequest.emit();
  }

  displayWith(artifactColumn) {
    return artifactColumn ? artifactColumn.aliasName || artifactColumn.displayName : '';
  }
}
