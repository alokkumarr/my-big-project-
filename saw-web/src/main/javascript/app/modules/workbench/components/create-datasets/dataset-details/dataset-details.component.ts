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
  private myHeight: Number;
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
    this.myHeight = window.screen.availHeight - 345;
    this.detailsFormGroup = new FormGroup({
      fieldSeperatorControl: new FormControl('', Validators.required),
      hederSizeControl: new FormControl('1', Validators.required),
      fieldNamesLineControl: new FormControl('1', Validators.required),
      lineSeperatorControl: new FormControl('\n', Validators.required),
      quoteCharControl: new FormControl('', Validators.required),
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

  onResize(event) {
    this.myHeight = window.screen.availHeight - 345;
  }

  addFormat(event: MatChipInputEvent): void {
    let input = event.input;
    let value = event.value;

    // Add Format
    if ((value || '').trim()) {
      this.previewConfig.csvInspector.dateFormats.push(value.trim());
    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  removeFormat(format: any): void {
    let index = this.previewConfig.csvInspector.dateFormats.indexOf(format);

    if (index >= 0) {
      this.previewConfig.csvInspector.dateFormats.splice(index, 1);
    }
  }

  previewDialog(fileDetails): void {
    const path = fileDetails.cat === 'root' ? fileDetails.name : `${fileDetails.cat}/${fileDetails.name}`;
    this.workBench.getRawPreviewData(this.userProject, path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
        data: {
          title: fileDetails.name,
          rawData: data.samplesRaw
        }
      });
    });
  }

  onFormValid(data) {
    if (!isUndefined(this.selFiles)) {
      this.previewConfig.csvInspector.delimiter = data.fieldSeperatorControl;
      this.previewConfig.csvInspector.fieldNamesLine = data.fieldNamesLineControl;
      this.previewConfig.csvInspector.hederSize = data.hederSizeControl;
      this.previewConfig.csvInspector.lineSeparator = data.lineSeperatorControl;
      this.previewConfig.csvInspector.quoteEscapeChar = data.escapeCharControl;
      this.previewConfig.csvInspector.quoteChar = data.quoteCharControl
      this.onDetailsFilled.emit({ detailsFilled: true, details: this.previewConfig });
    }
  }
}