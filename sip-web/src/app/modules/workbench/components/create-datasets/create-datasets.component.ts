import { MatDialog } from '@angular/material';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ENTER, COMMA, SEMICOLON } from '@angular/cdk/keycodes';
import {
  MatAutocompleteSelectedEvent,
  MatAutocomplete
} from '@angular/material/autocomplete';
import { MatChipInputEvent } from '@angular/material/chips';

import { Component, ViewChild, OnInit, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

import * as isUndefined from 'lodash/isUndefined';
import * as cloneDeep from 'lodash/cloneDeep';
import * as reject from 'lodash/reject';
import * as difference from 'lodash/difference';
import { CSV_CONFIG, PARSER_CONFIG } from '../../wb-comp-configs';

import { ParserPreviewComponent } from './parser-preview/parser-preview.component';
import { DatasetDetailsComponent } from './dataset-details/dataset-details.component';
import { RawpreviewDialogComponent } from './rawpreview-dialog/rawpreview-dialog.component';
import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { isUnique } from 'src/app/common/validators';
import { DS_NAME_PATTERN, DS_NAME_PATTERN_HINT_ERROR } from '../../consts';
@Component({
  selector: 'create-datasets',
  templateUrl: './create-datasets.component.html',
  styleUrls: ['./create-datasets.component.scss']
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
  public listOfDS: any[] = [];
  public folNamePattern = cloneDeep(DS_NAME_PATTERN);
  public dsNameHintAndError = cloneDeep(DS_NAME_PATTERN_HINT_ERROR);
  public allowableTags: Array<any> = [];
  public tagSeparatorKeysCodes = [ENTER, COMMA, SEMICOLON];
  public selectedTags: Array<any> = [];
  public autoCompleteTagList$: Observable<any[]>;

  constructor(
    public router: Router,
    public dialog: MatDialog,
    public workBench: WorkbenchService,
    public notify: ToastService
  ) {}

  @ViewChild('previewComponent', { static: false })
  public previewComponent: ParserPreviewComponent;
  @ViewChild('detailsComponent', { static: true })
  public detailsComponent: DatasetDetailsComponent;

  @ViewChild('tagsInput', { static: true }) tagInput: ElementRef<
    HTMLInputElement
  >;
  @ViewChild('auto', { static: true }) matAutocomplete: MatAutocomplete;

  ngOnInit() {
    this.getListOfAllowableTags();
    this.getListOfDatasets();
    this.csvConfig = cloneDeep(CSV_CONFIG);
    this.parserConf = cloneDeep(PARSER_CONFIG);
    this.nameFormGroup = new FormGroup({
      nameControl: new FormControl(
        '',
        Validators.compose([
          Validators.required,
          Validators.pattern(this.folNamePattern),
          Validators.minLength(3),
          Validators.maxLength(25)
        ]),
        isUnique(this.doesDSNameExist.bind(this), val => val, '')
      ),
      descControl: new FormControl('', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(99)
      ]),
      dsTagCtrl: new FormControl('')
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
    } else if (
      event.selectedIndex === 2 &&
      event.previouslySelectedIndex === 3
    ) {
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
      name: this.nameFormGroup.value.nameControl,
      description: this.nameFormGroup.value.descControl,
      component: 'parser',
      configuration: {
        fields: this.fieldsConf.fields,
        file: this.fieldsConf.info.file,
        lineSeparator: this.fieldsConf.lineSeparator,
        delimiter: this.fieldsConf.delimiter,
        quoteChar: this.fieldsConf.quoteChar,
        quoteEscape: this.fieldsConf.quoteEscapeChar,
        headerSize: this.fieldsConf.headerSize,
        userdata: {
          tags: this.selectedTags
        }
      }
    };
    this.parserConf.outputs[0].description = this.nameFormGroup.value.descControl;
    this.workBench.triggerParser(payload).subscribe(data => {
      this.notify.info('Parser_triggered_successfully', 'Parsing', {
        hideDelay: 9000
      });
    });
    this.router.navigate(['workbench', 'dataobjects']);
  }

  backtoLists() {
    this.router.navigate(['workbench', 'dataobjects']);
  }

  /**
   * Convert the dataset name to uppercase.
   * Added as part of SIP-9001.
   */
  changedToUppercase(event) {
    this.nameFormGroup.patchValue({
      nameControl: event.target.value.toUpperCase()
    });
  }

  getListOfDatasets() {
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.listOfDS = data.map(res => {
        return res.system.name;
      });
    });
  }

  /**
   *
   * @param name Provides DS name in NameFormGroup.
   */
  doesDSNameExist(name: string): Observable<boolean> {
    return of(this.listOfDS.includes(name));
  }

  /**
   * Get the list of allowable tags. Added as part of SIP-8963.
   * Setting autoCompleteTagList observable here as when user click on
   * input field for the first time autocomplete doesn't work,
   * as `allowableTags` does not have initial value.
   * So making sure that autocomplete only works when
   * all the allowable tags are present.
   */
  getListOfAllowableTags() {
    this.workBench.getAllowableTagsList().subscribe(({ allowableTags }) => {
      this.allowableTags = allowableTags;
      this.autoCompleteTagList$ = this.nameFormGroup
        .get('dsTagCtrl')
        .valueChanges.pipe(
          startWith(''),
          map((tag: string | null) =>
            tag
              ? this.filterTag(tag)
              : difference(this.allowableTags, this.selectedTags)
          )
        );
    });
  }

  /**
   * @param targetIndex Index of tag/Chip which needs to be removed.
   * Added as part of SIP-8963
   */
  removeTag(targetIndex) {
    if (targetIndex >= 0) {
      this.selectedTags = reject(
        this.selectedTags,
        (_, index) => index === targetIndex
      );
    }
  }

  /**
   * @param event mat autocomplete event
   * Added as part of SIP-8963
   */
  addTag(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;

    // Add tag
    if ((value || '').trim()) {
      this.selectedTags.push(value.trim());
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    this.nameFormGroup.get('dsTagCtrl').setValue('');
  }

  /**
   * @param filterVal input search text.
   * Added as part of SIP-8963
   */
  filterTag(filterVal) {
    const filterValue = filterVal.toLowerCase();
    const diff = difference(this.allowableTags, this.selectedTags);
    return diff.filter(tag => tag.toLowerCase().indexOf(filterValue) >= 0);
  }

  tagSelected(event: MatAutocompleteSelectedEvent) {
    this.selectedTags.push(event.option.value);
    this.tagInput.nativeElement.value = '';
    this.nameFormGroup.get('dsTagCtrl').setValue('');
  }

  trackByValue(index, value) {
    return value;
  }
}
