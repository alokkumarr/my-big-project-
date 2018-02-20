declare function require(string): string;

import { Component, Inject, ViewChild, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import * as cloneDeep from 'lodash/cloneDeep';

import { CSV_CONFIG , PARSER_CONFIG} from '../../wb-comp-configs'

import { ParserPreviewComponent } from './parser-preview/parser-preview.component';
import { RawpreviewDialogComponent } from './rawpreview-dialog/rawpreview-dialog.component'
import { WorkbenchService } from '../../services/workbench.service';

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
  private parsedPreview = new BehaviorSubject([]);
  private toAdd: Subject<any> = new Subject();
  private fieldsConf: any;
  private parserConf: any;
  public nameFormGroup: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<CreateDatasetsComponent>,
    private dialog: MatDialog,
    private workBench: WorkbenchService,
    private formBuilder: FormBuilder
  ) { }

  @ViewChild('previewComponent') private previewComponent: ParserPreviewComponent;
  
  ngOnInit() {
    this.csvConfig = cloneDeep(CSV_CONFIG); 
    this.parserConf = cloneDeep(PARSER_CONFIG);
    this.nameFormGroup = new FormGroup({
      nameControl: new FormControl('', [Validators.required, Validators.minLength(3), Validators.maxLength(18)]),
      descControl: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(50)])
    });
  }

  stepChanged(event) {
    if (event.selectedIndex === 2 && event.previouslySelectedIndex !== 3) {
      this.getParsedPreview();
    } else if (event.selectedIndex === 3) {
      this.previewComponent.toAdd();
    }
  } 

  markSelectDone(data): void {
    this.selectFullfilled = data.selectFullfilled;
    this.selectedFiles = data.selectedFiles;
    this.csvConfig.file = data.filePath;
  }

  markDetailsDone(data): void {
    this.detailsFilled = data.detailsFilled;
    this.details = data.details;
  }

  getParsedPreview() {
    if (this.detailsFilled) {
      this.workBench.getParsedPreviewData(this.userProject, this.details).subscribe(data => {
        this.parsedPreview.next(data);
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
        data: {
          title: fileDetails.name,
          rawData: data.data
        }
      });
    });
  }

  triggerParser() {
  }
}
