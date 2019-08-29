import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SftpSourceComponent } from './sftp-source.component';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { E2eDirective } from 'src/app/common/directives';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { CHANNEL_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';

describe('SftpSourceComponent', () => {
  let component: SftpSourceComponent;
  let fixture: ComponentFixture<SftpSourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SftpSourceComponent, E2eDirective],
      imports: [FormsModule, MaterialModule, NoopAnimationsModule],
      providers: [{ provide: DatasourceService, useValue: {} }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SftpSourceComponent);
    component = fixture.componentInstance;
    component.channelData = {};
    component.opType = CHANNEL_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
