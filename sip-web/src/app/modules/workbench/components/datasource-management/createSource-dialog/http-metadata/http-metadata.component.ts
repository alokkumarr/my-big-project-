import { Component, OnInit, Input } from '@angular/core';
import { HTTP_METHODS } from 'src/app/modules/workbench/models/workbench.interface';
import { FormGroup, FormArray, FormBuilder, Validators } from '@angular/forms';
import {
  CONTENT_TYPE_VALUES,
  STANDARD_HEADERS
} from 'src/app/common/http-headers';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Component({
  selector: 'http-metadata',
  templateUrl: './http-metadata.component.html',
  styleUrls: ['./http-metadata.component.scss']
})
export class HttpMetadataComponent implements OnInit {
  httpMethods: HTTP_METHODS[] = [
    HTTP_METHODS.GET,
    HTTP_METHODS.POST
    // HTTP_METHODS.PUT,
    // HTTP_METHODS.PATCH,
    // HTTP_METHODS.DELETE
  ];

  filteredHeaderFields: Observable<string[]>[] = [];
  filteredHeaderValues: Observable<string[]>[] = [];

  @Input() requiredFields: Array<string>;
  @Input() parentForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {
    (this.parentForm.get('headerParameters') as FormArray).controls.forEach(
      headerControl =>
        this.generateHeaderAutoCompleteFilter(headerControl as FormGroup)
    );
  }

  /**
   * Creates and attaches autocomplete filters for header field names
   * and field values.
   *
   * @param {FormGroup} headerControl
   * @memberof HttpMetadataComponent
   */
  generateHeaderAutoCompleteFilter(headerControl: FormGroup) {
    // Gets form control for header field name (key) to attach
    // change listener to.
    const headerFieldControl = headerControl.get('key');
    this.filteredHeaderFields.push(
      headerFieldControl.valueChanges.pipe(
        startWith(headerFieldControl.value),
        map(value => this.filterHeaderFields(value))
      )
    );

    // Same deal with header field value.
    const headerValueControl = headerControl.get('value');
    this.filteredHeaderValues.push(
      headerValueControl.valueChanges.pipe(
        startWith(headerValueControl.value),
        map(value => this.filterValueFields(value))
      )
    );
  }

  isRequired(fieldName: string): boolean {
    return (this.requiredFields || []).includes(fieldName);
  }

  get headerParams() {
    return this.parentForm.get('headerParameters') as FormArray;
  }

  get queryParams() {
    return this.parentForm.get('queryParameters') as FormArray;
  }

  get showBodyParams() {
    return [HTTP_METHODS.POST, HTTP_METHODS.PUT, HTTP_METHODS.PATCH].includes(
      this.parentForm.get('httpMethod').value
    );
  }

  get canAddHeader() {
    return this.headerParams.valid;
  }

  get canAddQueryParam() {
    return this.queryParams.valid;
  }

  /**
   * Adds new header form group to headers form array, and sets up
   * autocomplete filters for header field name (key) and value.
   *
   * @memberof HttpMetadataComponent
   */
  addHeader() {
    const headers = this.parentForm.get('headerParameters') as FormArray;
    const headerControl = this.formBuilder.group({
      key: ['', Validators.required],
      value: ['', Validators.required]
    });
    this.generateHeaderAutoCompleteFilter(headerControl);
    headers.push(headerControl);
  }

  /**
   * Removes header row from form and its autocomplete listeners.
   *
   * @param {number} index
   * @memberof HttpMetadataComponent
   */
  removeHeader(index: number) {
    const headers = this.parentForm.get('headerParameters') as FormArray;

    headers.removeAt(index);

    // Remove header autocomplete listeners. This happens after
    // deleting header from form, so that form control can
    // unsubscribe from filter observables first.
    this.filteredHeaderFields.splice(index, 1);
    this.filteredHeaderValues.splice(index, 1);
  }

  httpMethodChanged() {
    this.parentForm.get('bodyParameters.content').updateValueAndValidity();
  }

  addQueryParam() {
    const params = this.parentForm.get('queryParameters') as FormArray;
    params.push(
      this.formBuilder.group({
        key: ['', Validators.required],
        value: ['']
      })
    );
  }

  removeQueryParam(index: number) {
    const params = this.parentForm.get('queryParameters') as FormArray;
    params.removeAt(index);
  }

  /**
   * Autocomplete filter for header field names.
   *
   * @private
   * @param {string} value
   * @returns {string[]}
   * @memberof HttpMetadataComponent
   */
  private filterHeaderFields(value: string): string[] {
    const filterValue = value.toLowerCase();

    return STANDARD_HEADERS.filter(option =>
      option.toLowerCase().includes(filterValue)
    );
  }

  /**
   * Autocomplete filter for header field values.
   *
   * @private
   * @param {string} value
   * @returns {string[]}
   * @memberof HttpMetadataComponent
   */
  private filterValueFields(value: string): string[] {
    const filterValue = value.toLowerCase();

    return CONTENT_TYPE_VALUES.filter(option =>
      option.toLowerCase().includes(filterValue)
    );
  }
}