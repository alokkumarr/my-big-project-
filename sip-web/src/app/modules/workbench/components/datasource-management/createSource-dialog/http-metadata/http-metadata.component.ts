import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import {
  HTTP_METHODS,
  AUTHORIZATION_TYPES,
  APIRouteMetadata
} from 'src/app/modules/workbench/models/workbench.interface';
import {
  FormGroup,
  FormArray,
  FormBuilder,
  Validators,
  FormControl
} from '@angular/forms';
import {
  CONTENT_TYPE_VALUES,
  STANDARD_HEADERS
} from 'src/app/common/http-headers';
import { Observable, Subscription } from 'rxjs';
import { map, startWith, debounceTime } from 'rxjs/operators';
import { requireIf } from 'src/app/common/validators';
import * as find from 'lodash/find';

const AUTHORIZATION_HEADER_KEY = 'Authorization';

@Component({
  selector: 'http-metadata',
  templateUrl: './http-metadata.component.html',
  styleUrls: ['./http-metadata.component.scss']
})
export class HttpMetadataComponent implements OnInit, OnDestroy {
  httpMethods: HTTP_METHODS[] = [
    HTTP_METHODS.GET,
    HTTP_METHODS.POST
    // HTTP_METHODS.PUT,
    // HTTP_METHODS.PATCH,
    // HTTP_METHODS.DELETE
  ];

  authorizationTypes = AUTHORIZATION_TYPES;
  authorizationTypeValues = Object.values(AUTHORIZATION_TYPES);
  authorizationForm: FormGroup;

  filteredHeaderFields: Observable<string[]>[] = [];
  filteredHeaderValues: Observable<string[]>[] = [];

  provisionalHeaders: { key: String; value: String }[] = [];

  subscriptions: Subscription[] = [];

  @Input() requiredFields: Array<string>;
  @Input() parentForm: FormGroup;

  static getInitialProvisionalHeaders(
    headerParams: APIRouteMetadata['headerParameters']
  ): {
    provisionalHeaders: APIRouteMetadata['headerParameters'];
    headers: APIRouteMetadata['headerParameters'];
  } {
    const headers = [];
    const provisionalHeaders = [];
    headerParams.forEach(param => {
      if (param.key !== 'Authorization') {
        headers.push(param);
        return;
      }

      const userAuth = param.value.match(/^Basic (.*)/);
      if (!userAuth) {
        headers.push(param);
        return;
      }

      const [userName, password] = atob(userAuth[1]).split(':');
      if (!userName || !password) {
        headers.push(param);
        return;
      }

      provisionalHeaders.push(param);
    });
    return { provisionalHeaders, headers };
  }

  constructor(private formBuilder: FormBuilder) {
    this.createAuthForm();
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  ngOnInit() {
    (this.parentForm.get(
      'headerParameters'
    ) as FormArray).controls.forEach(headerControl =>
      this.generateHeaderAutoCompleteFilter(headerControl as FormGroup)
    );

    this.initializeProvisionalHeaders(
      this.parentForm.get('provisionalHeaders').value
    );
  }

  initializeProvisionalHeaders(headers) {
    const userAuth = find(
      headers,
      header =>
        header.key === AUTHORIZATION_HEADER_KEY && /^Basic/.test(header.value)
    );

    if (!userAuth) {
      return;
    }

    this.provisionalHeaders = [userAuth];
    const [userName, password] = atob(userAuth.value.split(' ')[1]).split(':');
    this.authorizationForm.patchValue({
      type: AUTHORIZATION_TYPES.BASIC,
      userName,
      password
    });
  }

  /**
   * Creates form for authorization options. This is a convinience layer
   * over what's achievable with normal headers.
   *
   * @memberof HttpMetadataComponent
   */
  createAuthForm() {
    this.authorizationForm = this.formBuilder.group({
      type: [AUTHORIZATION_TYPES[Object.keys(AUTHORIZATION_TYPES)[0]]],
      userName: [
        '',
        [requireIf('type', val => val === AUTHORIZATION_TYPES.BASIC)]
      ],
      password: [
        '',
        [requireIf('type', val => val === AUTHORIZATION_TYPES.BASIC)]
      ]
    });

    this.subscriptions.push(
      this.authorizationForm.get('type').valueChanges.subscribe(() => {
        this.authUsername.updateValueAndValidity();
        this.authPassword.updateValueAndValidity();
      })
    );

    this.subscriptions.push(
      this.authorizationForm.valueChanges
        .pipe(debounceTime(500))
        .subscribe(() => {
          this.updateProvisionalHeaders(this.authorizationForm.value);
        })
    );
  }

  /**
   * Update the provisional headers based on authorization form value
   *
   * @memberof HttpMetadataComponent
   */
  updateProvisionalHeaders({ type, userName, password }) {
    switch (type) {
      case AUTHORIZATION_TYPES.BASIC:
        this.addUserAuthHeader(userName, password);
        break;
      case AUTHORIZATION_TYPES.NONE:
      default:
        this.removeAuthHeader();
        break;
    }
  }

  /**
   * If username and password are valid, adds a new
   * authorization header. Replaces previous authorization
   * header if it was set.
   *
   * @param {string} userName
   * @param {string} password
   * @returns
   * @memberof HttpMetadataComponent
   */
  addUserAuthHeader(userName: string, password: string) {
    this.removeAuthHeader();
    if (!userName || !password) {
      return;
    }

    this.provisionalHeaders = [
      {
        key: AUTHORIZATION_HEADER_KEY,
        value: `Basic ${btoa(userName + ':' + password)}`
      },
      ...this.provisionalHeaders
    ];
    this.updateFormArray();
  }

  /**
   * Removes the provisional authorization header
   *
   * @memberof HttpMetadataComponent
   */
  removeAuthHeader() {
    this.provisionalHeaders = this.provisionalHeaders.filter(
      header => header.key !== AUTHORIZATION_HEADER_KEY
    );
    this.updateFormArray();
  }

  updateFormArray() {
    const array = this.formBuilder.array(
      this.provisionalHeaders.map(({ key, value }) =>
        this.formBuilder.group({
          key: [key],
          value: [value]
        })
      )
    );
    this.parentForm.setControl('provisionalHeaders', array);
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
    setTimeout(() => {
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
    }, 100);
  }

  isRequired(fieldName: string): boolean {
    return (this.requiredFields || []).includes(fieldName);
  }

  get provisionalHeaderParams() {
    return this.parentForm.get('provisionalHeaders') as FormArray;
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

  get authUsername(): FormControl {
    return this.authorizationForm.get('userName') as FormControl;
  }

  get authPassword(): FormControl {
    return this.authorizationForm.get('password') as FormControl;
  }

  get authType(): FormControl {
    return this.authorizationForm.get('type') as FormControl;
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
