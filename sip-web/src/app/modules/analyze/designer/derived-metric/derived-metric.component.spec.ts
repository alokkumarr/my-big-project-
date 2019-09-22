declare const ace: any;
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DerivedMetricComponent } from './derived-metric.component';
import { MaterialModule } from 'src/app/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { AceEditorModule } from 'ng2-ace-editor';
import { MatDialogRef } from '@angular/material';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DerivedMetricComponent', () => {
  let component: DerivedMetricComponent;
  let fixture: ComponentFixture<DerivedMetricComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DerivedMetricComponent],
      imports: [
        AceEditorModule,
        MaterialModule,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [{ provide: MatDialogRef, useValue: {} }]
    }).compileComponents();
  }));

  beforeEach(() => {
    ace.require = () => ({
      addCompleter: () => {},
      setCompleters: () => {}
    });
    fixture = TestBed.createComponent(DerivedMetricComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
