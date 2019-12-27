import { Component, OnInit, Input } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { DSKFilterGroup, DSKFilterField } from '../dsk-filter.model';

import { PopperContent } from 'ngx-popper';
import { MatChipInputEvent } from '@angular/material';

@Component({
  selector: 'dsk-filter-group',
  templateUrl: './dsk-filter-group.component.html',
  styleUrls: ['./dsk-filter-group.component.scss']
})
export class DskFilterGroupComponent implements OnInit {
  @Input() filterGroup: DSKFilterGroup;
  readonly separatorKeysCodes: number[] = [ENTER, COMMA];
  constructor() {}

  ngOnInit() {}

  addField(popper: PopperContent) {
    popper.hide();
  }

  addGroup(popper: PopperContent) {
    popper.hide();
  }

  addValue(childId: number, event: MatChipInputEvent) {
    const input = event.input;
    const value = event.value;
    if ((value || '').trim()) {
      (<DSKFilterField>this.filterGroup.booleanQuery[childId]).model.value.push(
        value
      );
    }

    if (input) {
      input.value = '';
    }
  }
}
