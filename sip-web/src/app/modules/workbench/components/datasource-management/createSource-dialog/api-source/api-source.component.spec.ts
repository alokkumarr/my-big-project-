import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';

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

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, MaterialModule, NoopAnimationsModule],
      declarations: [ApiSourceComponent, HttpMetadataComponent, E2eDirective],
      providers: [{ provide: DatasourceService, useValue: {} }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiSourceComponent);
    component = fixture.componentInstance;
    component.channelData = {};
    component.opType = CHANNEL_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
