import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpMetadataComponent } from './http-metadata.component';
import { FormsModule, FormBuilder } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { E2eDirective } from 'src/app/common/directives';
import { AUTHORIZATION_TYPES } from 'src/app/modules/workbench/models/workbench.interface';

describe('HttpMetadataComponent', () => {
  let component: HttpMetadataComponent;
  let fixture: ComponentFixture<HttpMetadataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HttpMetadataComponent, E2eDirective],
      imports: [FormsModule, MaterialModule, NoopAnimationsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HttpMetadataComponent);
    component = fixture.componentInstance;

    const formBuilder: FormBuilder = TestBed.get(FormBuilder);
    component.parentForm = formBuilder.group({
      httpMethod: '',
      apiEndPoint: '',
      headerParameters: formBuilder.array([]),
      queryParameters: formBuilder.array([]),
      bodyParameters: formBuilder.group({
        content: ''
      }),
      provisionalHeaders: formBuilder.array([])
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('getInitialProvisionalHeaders', () => {
    it('should get provisional headers if possible', () => {
      const headerValue = `Basic ${btoa('abc:123')}`;
      const {
        provisionalHeaders,
        headers
      } = HttpMetadataComponent.getInitialProvisionalHeaders([
        { key: 'Authorization', value: headerValue }
      ]);
      expect(provisionalHeaders.length).toEqual(1);
      expect(headers.length).toEqual(0);
    });

    it('should not extract provisional headers for non-conforming values', () => {
      const headerValue = `Basic ${btoa('abc123')}`; // no : present - so no username/password boundary exists
      const {
        provisionalHeaders,
        headers
      } = HttpMetadataComponent.getInitialProvisionalHeaders([
        { key: 'Authorization', value: headerValue }
      ]);
      expect(provisionalHeaders.length).toEqual(0);
      expect(headers.length).toEqual(1);
    });

    it('should not extract provisional headers if none exist', () => {
      const headerValue = `Bearer ${btoa('abc123')}`; // Bearer is not Basic
      const {
        provisionalHeaders,
        headers
      } = HttpMetadataComponent.getInitialProvisionalHeaders([
        { key: 'Authorization', value: headerValue }
      ]);
      expect(provisionalHeaders.length).toEqual(0);
      expect(headers.length).toEqual(1);
    });

    it('should not extract provisional headers for non-authorization headers', () => {
      const headerValue = `Basic ${btoa('abc:123')}`;
      const {
        provisionalHeaders,
        headers
      } = HttpMetadataComponent.getInitialProvisionalHeaders([
        { key: 'Content-Type', value: headerValue }
      ]);
      expect(provisionalHeaders.length).toEqual(0);
      expect(headers.length).toEqual(1);
    });
  });

  describe('removeAuthHeader', () => {
    it('should remove auth header', () => {
      component.provisionalHeaders = [
        { key: 'Content-Type', value: 'abc' },
        { key: 'Authorization', value: '123' }
      ];
      component.removeAuthHeader();
      expect(component.provisionalHeaders.length).toEqual(1);
    });
  });

  describe('addUserAuthHeader', () => {
    it('should remove existing auth header if not valid', () => {
      const headerValue = `Bearer ${btoa('abc:123')}`; // Bearer is not Basic
      component.provisionalHeaders = [
        { key: 'Authorization', value: headerValue }
      ];
      component.addUserAuthHeader(null, 'abc');
      expect(component.provisionalHeaders.length).toEqual(0);
    });

    it('should replace existing auth header if valid', () => {
      const headerValue = `Bearer ${btoa('abc:123')}`; // Bearer is not Basic
      component.provisionalHeaders = [
        { key: 'Authorization', value: headerValue }
      ];
      component.addUserAuthHeader('xyz', 'abc');
      expect(component.provisionalHeaders.length).toEqual(1);
      expect(component.provisionalHeaders[0].value).not.toEqual(headerValue);
    });
  });

  describe('updateProvisionalHeaders', () => {
    it('should remove headers if type is set to none', () => {
      const headerValue = `Bearer ${btoa('abc:123')}`; // Bearer is not Basic
      component.provisionalHeaders = [
        { key: 'Authorization', value: headerValue }
      ];
      component.updateProvisionalHeaders({
        type: AUTHORIZATION_TYPES.NONE,
        userName: null,
        password: null
      });
      expect(component.provisionalHeaders.length).toEqual(0);
    });

    it('should replace header if correct type is selected and valid values are provided', () => {
      component.provisionalHeaders = [];
      component.updateProvisionalHeaders({
        type: AUTHORIZATION_TYPES.BASIC,
        userName: 'abc',
        password: 'def'
      });
      expect(component.provisionalHeaders.length).toEqual(1);
    });
  });

  describe('initializeProvisionalHeaders', () => {
    it('should not add any provisional headers if none present', () => {
      component.initializeProvisionalHeaders([
        { key: 'Content-Type', value: 'abc' }
      ]);
      expect(component.provisionalHeaders.length).toEqual(0);
    });

    it('should initialise provisional headers if User Auth header present', () => {
      component.initializeProvisionalHeaders([
        { key: 'Authorization', value: `Basic ${btoa('123:abc')}` }
      ]);
      expect(component.provisionalHeaders.length).toEqual(1);
    });

    it('addQueryParam should be called', () => {
      expect(typeof component.generateHeaderAutoCompleteFilter).toEqual('function');
    });
  });
});
