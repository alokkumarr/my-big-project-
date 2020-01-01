import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';

import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { ApiSourceComponent } from './api-source.component';
import { MaterialModule } from 'src/app/material.module';
import { HttpMetadataComponent } from '../http-metadata/http-metadata.component';
import { E2eDirective } from 'src/app/common/directives';
import { CHANNEL_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ApiSourceComponent', () => {
  let component: ApiSourceComponent;
  let fixture: ComponentFixture<ApiSourceComponent>;
  let formBuilder: FormBuilder;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MaterialModule, NoopAnimationsModule],
      declarations: [ApiSourceComponent, HttpMetadataComponent, E2eDirective],
      providers: [
        {
          provide: DatasourceService,
          useValue: { isDuplicateChannel: () => {} }
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiSourceComponent);
    formBuilder = TestBed.get(FormBuilder);
    component = fixture.componentInstance;
    component.channelData = { headerParameters: [] };
    component.opType = CHANNEL_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Validations', () => {
    it('should validate for protocol in hostname', () => {
      component.createForm();
      const hostName = component.detailsFormGroup.get('hostName');

      hostName.setValue('google.com');
      expect(hostName.valid).toEqual(false);

      hostName.setValue('http://google.com');
      expect(hostName.valid).toEqual(true);
    });

    it('should validate port number to always be a number', () => {
      component.createForm();
      const port = component.detailsFormGroup.get('portNo');

      port.setValue('abc');
      expect(port.valid).toEqual(false);

      port.setValue('1234');
      expect(port.valid).toEqual(true);
    });
  });

  it('should return provisional headers inside normal headers', () => {
    const array = formBuilder.array([
      formBuilder.group({
        key: ['123'],
        value: ['abc']
      })
    ]);
    component.detailsFormGroup.setControl('provisionalHeaders', array);

    const value = component.value;
    expect(value.headerParameters.length).toEqual(1);
    expect(value['provisionalHeaders']).toBeUndefined();
  });

  it('should not validate for old value for channelName when updating', () => {
    const dataSourceService = TestBed.get(DatasourceService);
    const spy = spyOn(dataSourceService, 'isDuplicateChannel').and.returnValue(
      null
    );
    component.opType = CHANNEL_OPERATION.UPDATE;
    component.channelData.channelName = 'oldName';
    component.createForm();
    component.detailsFormGroup.get('channelName').setValue('oldName');
    component.detailsFormGroup.get('channelName').updateValueAndValidity();
    expect(spy).not.toHaveBeenCalled();
  });
});
