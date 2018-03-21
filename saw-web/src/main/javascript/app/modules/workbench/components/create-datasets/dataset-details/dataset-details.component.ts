declare function require(string): string;

import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';
import { MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material';
import { ENTER, COMMA } from '@angular/cdk/keycodes';

import * as isUndefined from 'lodash/isUndefined';

import { RawpreviewDialogComponent } from '../rawpreview-dialog/rawpreview-dialog.component'
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./dataset-details.component.html');
require('./dataset-details.component.scss');

@Component({
  selector: 'dataset-details',
  template,
  styles: []
})

export class DatasetDetailsComponent implements OnInit {
  @Input() selFiles: Array<any>;
  @Input() previewConfig: any;
  private separatorKeysCodes = [ENTER, COMMA]; // tslint:disable-line
  public detailsFormGroup: FormGroup;
  private lineSeperator = 'lineFeed';

  constructor(
    public dialog: MatDialog,
    private workBench: WorkbenchService
  ) { }

  @Output() onDetailsFilled: EventEmitter<any> = new EventEmitter<any>();

  ngOnInit() {
    this.detailsFormGroup = new FormGroup({
      fieldSeperatorControl: new FormControl('', Validators.required),
      hederSizeControl: new FormControl('1', Validators.required),
      fieldNamesLineControl: new FormControl('1'),
      quoteCharControl: new FormControl(''),
      escapeCharControl: new FormControl('')
    });
    this.subcribeToFormChanges();
  }

  subcribeToFormChanges() {
    const detailsFormStatusChange = this.detailsFormGroup.statusChanges;
    detailsFormStatusChange.subscribe(status => {
      if (status === 'VALID') {
        this.onFormValid(this.detailsFormGroup.value);
      } else {
        this.onDetailsFilled.emit({ detailsFilled: false, details: this.previewConfig });
      }
    });
  }

  addFormat(event: MatChipInputEvent): void {
    let input = event.input;
    let value = event.value;

    // Add Format
    if ((value || '').trim()) {
      this.previewConfig.dateFormats.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  removeFormat(format: any): void {
    let index = this.previewConfig.dateFormats.indexOf(format);

    if (index >= 0) {
      this.previewConfig.dateFormats.splice(index, 1);
    }
  }

  previewDialog(fileDetails): void {
    const path = `${fileDetails.path}/${fileDetails.name}`;
    this.workBench.getRawPreviewData(path).subscribe(data => {
      this.dialog.open(RawpreviewDialogComponent, {
        minHeight: 500,
        minWidth: 600,
        data: {
          title: fileDetails.name,
          rawData: data.data
        }
      });
    });
  }

  onFormValid(data) {
    if (!isUndefined(this.selFiles)) {
      this.onDetailsFilled.emit({ detailsFilled: true, details: this.previewConfig });
    }
  }

  toPreview() {
    if (this.lineSeperator === 'lineFeed') {
      this.previewConfig.lineSeparator = '\n';
    } else if (this.lineSeperator === 'carriageReturn') {
      this.previewConfig.lineSeparator = '\r';
    } else {
      this.previewConfig.lineSeparator = '\r\n';
    }
    this.previewConfig.delimiter = this.detailsFormGroup.value.fieldSeperatorControl;
    this.previewConfig.fieldNamesLine = this.detailsFormGroup.value.fieldNamesLineControl;
    this.previewConfig.headerSize = this.detailsFormGroup.value.hederSizeControl;
    this.previewConfig.quoteEscapeChar = this.detailsFormGroup.value.escapeCharControl;
    this.previewConfig.quoteChar = this.detailsFormGroup.value.quoteCharControl;
    this.onDetailsFilled.emit({ detailsFilled: true, details: this.previewConfig });
  }
}
