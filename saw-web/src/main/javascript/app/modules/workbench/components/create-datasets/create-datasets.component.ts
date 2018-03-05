declare function require(string): string;

import { Component, Inject, ViewChild, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import * as cloneDeep from 'lodash/cloneDeep';
import * as merge from 'lodash/merge';
import * as omit from 'lodash/omit';
import * as set from 'lodash/set';

import { CSV_CONFIG, PARSER_CONFIG } from '../../wb-comp-configs'

import { ParserPreviewComponent } from './parser-preview/parser-preview.component';
import { DatasetDetailsComponent } from './dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './rawpreview-dialog/rawpreview-dialog.component'
import { WorkbenchService } from '../../services/workbench.service';
import { Parser } from '@angular/compiler/src/ml_parser/parser';

const template = require('./create-datasets.component.html');
require('./create-datasets.component.scss');

@Component({
  selector: 'create-datasets',
  template
})
export class CreateDatasetsComponent implements OnInit {
  public selectFullfilled: boolean = false;
  public detailsFilled: boolean = false;
  public selectedFiles: Array<any>;
  public details: any = [];
  private userProject: string = 'project2';
  private csvConfig: any;
  private parsedPreview = new Subject();
  private previewData: any;
  private toAdd: Subject<any> = new Subject();
  private fieldsConf: any;
  private parserConf: any;
  public nameFormGroup: FormGroup;
  private selectedIndex: number = 0;

  constructor(
    public dialogRef: MatDialogRef<CreateDatasetsComponent>,
    private dialog: MatDialog,
    private workBench: WorkbenchService,
    private formBuilder: FormBuilder
  ) { }

  @ViewChild('previewComponent') private previewComponent: ParserPreviewComponent;
  @ViewChild('detailsComponent') private detailsComponent: DatasetDetailsComponent;

  ngOnInit() {
    this.csvConfig = cloneDeep(CSV_CONFIG);
    this.parserConf = cloneDeep(PARSER_CONFIG);
    this.nameFormGroup = new FormGroup({
      nameControl: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(18)]),
      descControl: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(99)])
    });
  }

  stepChanged(event) {
    this.selectedIndex = event.selectedIndex;
    if (event.selectedIndex === 2 && event.previouslySelectedIndex !== 3) {
      this.detailsComponent.toPreview();
      this.getParsedPreview();
    } else if (event.selectedIndex === 3) {
      this.previewComponent.toAdd();
    } else if (event.selectedIndex === 2 && event.previouslySelectedIndex === 3) {
      this.parsedPreview.next(this.previewData);
    }
  }

  markSelectDone(data) {
    this.selectFullfilled = data.selectFullfilled;
    this.selectedFiles = data.selectedFiles;
    this.csvConfig.file = data.filePath;
  }

  markDetailsDone(data) {
    this.detailsFilled = data.detailsFilled;
    this.details = data.details;
  }

  getParsedPreview() {
    if (this.selectedIndex === 2) {
      this.workBench.getParsedPreviewData(this.userProject, this.details).subscribe(data => {
        this.previewData = data;
        setTimeout(() => {
          this.parsedPreview.next(this.previewData);
        });
      });
    }
  }

  getParserConfig(data) {
    this.fieldsConf = data;
  }

  previewDialog(fileDetails): void {
    const path = `${fileDetails.path}/${fileDetails.name}`;
    this.workBench.getRawPreviewData(this.userProject, path).subscribe(data => {
      const dialogRef = this.dialog.open(RawpreviewDialogComponent, {
        minHeight: 500,
        minWidth: 600,
        data: {
          title: fileDetails.name,
          rawData: data.data
        }
      });
    });
  }

  triggerParser() {
    const payload = {
      'name': this.nameFormGroup.value.nameControl,
      'component': 'parser',
      'configuration':
      {
        'fields': this.fieldsConf.fields,
        'file': this.fieldsConf.info.file,
        'lineSeparator': this.fieldsConf.lineSeparator,
        'delimiter': this.fieldsConf.delimiter,
        'quoteChar': this.fieldsConf.quoteChar,
        'quoteEscape': this.fieldsConf.quoteEscapeChar,
        'headerSize': this.fieldsConf.headerSize
      }
    };
    // this.parserConf.outputs[0].description = this.nameFormGroup.value.descControl;
    this.workBench.triggerParser(payload).subscribe(data => {
      //this.dialogRef.close();

    })
  }
}
