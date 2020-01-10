import {
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter,
  ViewContainerRef,
  ViewChild
} from '@angular/core';
import * as find from 'lodash/find';
import * as unset from 'lodash/unset';
import * as orderBy from 'lodash/orderBy';
import * as filter from 'lodash/filter';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import { ArtifactColumn, Filter, FilterModel } from '../../types';
import { TYPE_MAP, AGGREGATE_TYPES } from '../../../consts';

@Component({
  selector: 'designer-filter-row',
  templateUrl: './designer-filter-row.component.html',
  styleUrls: ['./designer-filter-row.component.scss']
})
export class DesignerFilterRowComponent implements OnInit {
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Output() public filterChange: EventEmitter<null> = new EventEmitter();
  @Output() public filterModelChange: EventEmitter<null> = new EventEmitter();
  @Input() public filter: Filter;
  @Input() public isInRuntimeMode: boolean;
  @Input() public supportsGlobalFilters: boolean;
  @Input() public showFilterOptions: boolean;

  @ViewChild('auto', { read: ViewContainerRef, static: true })
  _autoComplete: ViewContainerRef;
  artifactColumns: ArtifactColumn[];
  public TYPE_MAP = TYPE_MAP;
  formControl: FormControl;
  filteredColumns: Observable<ArtifactColumn[]>;

  @Input('artifactColumns')
  set _artifactColumns(data: ArtifactColumn[]) {
    this.artifactColumns = filter(
      orderBy(data, 'displayName'),
      /* Remove derived metric columns */
      col => !col.expression
    );
  }

  constructor() {
    this.displayWith = this.displayWith.bind(this);
  }

  ngOnInit() {
    const target = find(
      this.artifactColumns,
      ({ columnName }) => columnName === this.filter.columnName
    );
    this.formControl = new FormControl({
      value: target,
      disabled: this.isInRuntimeMode
    });
    this.filteredColumns = this.formControl.valueChanges.pipe(
      startWith<string | ArtifactColumn>(''),
      map(value => (typeof value === 'string' ? value : value.displayName)),
      map(name => (name ? this.nameFilter(name) : this.artifactColumns.slice()))
    );
    if (this.filter.isRuntimeFilter) {
      delete this.filter.model;
    }
  }

  clearInput() {
    this.formControl.setValue('');
    unset(this.filter, 'columnName');
    unset(this.filter, 'type');
    unset(this.filter, 'model');
    this.filter.isRuntimeFilter = false;
    this.filterChange.emit();
  }

  nameFilter(name: string): ArtifactColumn[] {
    return this.artifactColumns.filter(option => {
      const optionName = option.displayName;
      return optionName.toLowerCase().indexOf(name.toLowerCase()) > -1;
    });
  }

  onArtifactColumnSelected(columnName) {
    const target: ArtifactColumn = find(
      this.artifactColumns,
      column => column.columnName === columnName
    );
    this.filter.columnName = target.columnName;
    this.filter.type = target.type;
    if (this.filter.isRuntimeFilter || this.filter.isGlobalFilter) {
      delete this.filter.model;
    } else {
      this.filter.model = null;
    }
    this.filterChange.emit();
  }

  onFilterModelChange(filterModel: FilterModel) {
    this.filter.model = filterModel;
    this.filterModelChange.emit();
  }

  onGlobalCheckboxToggle(filter: Filter, checked: boolean) {
    if (!this.supportsGlobalFilters) {
      return;
    }
    filter.isGlobalFilter = checked;
    if (checked) {
      delete filter.model;
    }
    this.filterModelChange.emit();
  }

  onRuntimeCheckboxToggle(filter: Filter, checked: boolean) {
    filter.isRuntimeFilter = checked;
    delete filter.model;
    this.filterModelChange.emit();
  }

  onOptionalCheckboxToggle(filter: Filter, checked: boolean) {
    filter.isOptional = checked;
    this.filterModelChange.emit();
  }

  get supportedAggregates() {
    return AGGREGATE_TYPES;
  }

  onAggregateSelected(aggregate: string) {
    this.filter.aggregate = aggregate;
    this.filterModelChange.emit();
  }

  remove() {
    this.removeRequest.emit();
  }

  displayWith(artifactColumn) {
    return artifactColumn ? artifactColumn.displayName : '';
  }
}
