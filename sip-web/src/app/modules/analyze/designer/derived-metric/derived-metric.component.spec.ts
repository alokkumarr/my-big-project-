declare const ace: any;
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DerivedMetricComponent } from './derived-metric.component';
import { MaterialModule } from 'src/app/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('DerivedMetricComponent', () => {
  let component: DerivedMetricComponent;
  let fixture: ComponentFixture<DerivedMetricComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DerivedMetricComponent],
      imports: [MaterialModule, ReactiveFormsModule, NoopAnimationsModule],
      providers: [{ provide: MatDialogRef, useValue: {} }],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    window['ace'] = {}; // override ace to avoid bringing in full library
    ace.require = () => ({
      addCompleter: () => {},
      setCompleters: () => {}
    });
    fixture = TestBed.createComponent(DerivedMetricComponent);
    component = fixture.componentInstance;
    component.editorOptions = {};
    component.editor = { getEditor: () => ({ resize: () => {} }) } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
