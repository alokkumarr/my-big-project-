declare function require(string): string;

import { Component, Input, OnInit, EventEmitter, Output, AfterViewInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { MatChipInputEvent } from '@angular/material';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

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
  private userProject: string = 'project2';
  private separatorKeysCodes = [ENTER, COMMA];
  public detailsFormGroup: FormGroup;

  constructor(
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private formBuilder: FormBuilder
  ) { }

  @Output() onDetailsFilled: EventEmitter<any> = new EventEmitter<any>();

  ngOnInit() {
    this.detailsFormGroup = new FormGroup({
      fieldSeperatorControl: new FormControl('', Validators.required),
      hederSizeControl: new FormControl('1', Validators.required),
      fieldNamesLineControl: new FormControl('1', Validators.required),
      lineSeperatorControl: new FormControl('\n', Validators.required),
      quoteCharControl: new FormControl('"', Validators.required),
      escapeCharControl: new FormControl('\\', Validators.required)
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
    this.workBench.getRawPreviewData(this.userProject, path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
        data: {
          title: fileDetails.name,
          rawData: data.data
        }
      });
    });
  }

  onFormValid(data) {
    if (!isUndefined(this.selFiles)) {
      this.previewConfig.delimiter = data.fieldSeperatorControl;
      this.previewConfig.fieldNamesLine = data.fieldNamesLineControl;
      this.previewConfig.headerSize = data.hederSizeControl;
      this.previewConfig.lineSeparator = data.lineSeperatorControl;
      this.previewConfig.quoteEscapeChar = data.escapeCharControl;
      this.previewConfig.quoteChar = data.quoteCharControl
      this.onDetailsFilled.emit({ detailsFilled: true, details: this.previewConfig });
    }
  }
}