
import { MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import * as cloneDeep from 'lodash/cloneDeep';

import { Component, ViewChild, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import * as isUndefined from 'lodash/isUndefined';

import { CSV_CONFIG, PARSER_CONFIG } from '../../wb-comp-configs';

import { ParserPreviewComponent } from './parser-preview/parser-preview.component';
import { DatasetDetailsComponent } from './dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './rawpreview-dialog/rawpreview-dialog.component';
import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';


const style = require('./create-datasets.component.scss');

@Component({
  selector: 'create-datasets',
  templateUrl: './create-datasets.component.html',
  styles: [
    `:host {
      width: 100%;
      height: 100%;
      display: flex;
      flex-direction: column;
    }`,
    style
  ]
})
export class CreateDatasetsComponent implements OnInit {
  public selectFullfilled = false;
  public detailsFilled = false;
  public previewDone = false;
  public selectedFiles: Array<any>;
  public details: any = [];
  public csvConfig: any;
  public parsedPreview = new BehaviorSubject([]);
  public previewData: any;
  public fieldsConf: any;
  public parserConf: any; // tslint:disable-line
  public nameFormGroup: FormGroup;
  public selectedIndex = 0;
  public folNamePattern = '[A-Za-z0-9]+';

  constructor(
    public router: Router,
    public dialog: MatDialog,
    public workBench: WorkbenchService,
    public notify: ToastService
  ) { }

  @ViewChild('previewComponent') public previewComponent: ParserPreviewComponent;
  @ViewChild('detailsComponent') public detailsComponent: DatasetDetailsComponent;

  ngOnInit() {
    this.csvConfig = cloneDeep(CSV_CONFIG);
    this.parserConf = cloneDeep(PARSER_CONFIG);
    this.nameFormGroup = new FormGroup({
      nameControl: new FormControl('', [Validators.required, Validators.pattern(this.folNamePattern), Validators.minLength(3), Validators.maxLength(25)]),
      descControl: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(99)])
    });
  }

  stepChanged(event) {
    this.selectedIndex = event.selectedIndex;
    if (event.selectedIndex === 2 && event.previouslySelectedIndex !== 3) {
      this.detailsComponent.toPreview();
      this.previewDone = false;
      this.parsedPreview.next([]);
      this.getParsedPreview();
    } else if (event.selectedIndex === 3) {
      this.previewComponent.toAdd();
    } else if (event.selectedIndex === 2 && event.previouslySelectedIndex === 3) {
      this.previewDone = true;
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
      this.workBench.getParsedPreviewData(this.details).subscribe(data => {
        this.previewData = data;
        if (!isUndefined(data.samplesParsed)) {
          this.previewDone = true;
        } else {
          this.previewDone = false;
        }
        this.parsedPreview.next([this.previewData, this.details.file]);
      });
    }
  }

  getParserConfig(data) {
    this.fieldsConf = data;
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

  triggerParser() {
    const payload = {
      'name': this.nameFormGroup.value.nameControl,
      'description': this.nameFormGroup.value.descControl,
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
    this.parserConf.outputs[0].description = this.nameFormGroup.value.descControl;
    this.workBench.triggerParser(payload).subscribe(data => {
      this.notify.info('Parser_triggered_successfully', 'Parsing', { hideDelay: 9000 });
    });
    this.router.navigate(['workbench', 'dataobjects']);
  }

  backtoLists() {
    this.router.navigate(['workbench', 'dataobjects']);
  }
}
