import { Component, OnInit, Input } from '@angular/core';
import { HTTP_METHODS } from 'src/app/modules/workbench/models/workbench.interface';
import { FormGroup, FormArray, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'http-metadata',
  templateUrl: './http-metadata.component.html',
  styleUrls: ['./http-metadata.component.scss']
})
export class HttpMetadataComponent implements OnInit {
  httpMethods: HTTP_METHODS[] = [
    HTTP_METHODS.GET,
    HTTP_METHODS.POST,
    HTTP_METHODS.PUT,
    HTTP_METHODS.PATCH,
    HTTP_METHODS.DELETE
  ];

  @Input() requiredFields: Array<string>;

  @Input() parentForm: FormGroup;

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {}

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

  addHeader() {
    const headers = this.parentForm.get('headerParameters') as FormArray;
    headers.push(
      this.formBuilder.group({
        key: ['', Validators.required],
        value: ['', Validators.required]
      })
    );
  }

  removeHeader(index: number) {
    const headers = this.parentForm.get('headerParameters') as FormArray;
    headers.removeAt(index);
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
}
