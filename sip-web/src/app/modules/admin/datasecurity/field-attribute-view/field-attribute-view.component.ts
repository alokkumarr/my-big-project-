import { Component, Input, OnInit, OnChanges } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { DSKFilterGroup } from '../dsk-filter.model';
import { DataSecurityService } from '../datasecurity.service';

@Component({
  selector: 'field-attribute-view',
  templateUrl: './field-attribute-view.component.html',
  styleUrls: ['./field-attribute-view.component.scss']
})
export class FieldAttributeViewComponent implements OnInit, OnChanges {
  config: any;
  data: {};
  emptyState = true;
  previewString = '';

  @Input() groupSelected;

  dskFilterGroup: DSKFilterGroup;
  @Input('dskFilterGroup') set _dskFilterGroup(group: DSKFilterGroup) {
    this.dskFilterGroup = group;
    this.emptyState = isEmpty(this.dskFilterGroup);
    this.previewString = this.emptyState
      ? ''
      : this.datasecurityService.generatePreview(this.dskFilterGroup);
  }

  constructor(private datasecurityService: DataSecurityService) {}

  ngOnInit() {}

  ngOnChanges() {}
}
