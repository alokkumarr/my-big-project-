import { TestBed, ComponentFixture } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Store } from '@ngxs/store';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DesignerDataOptionFieldComponent } from './designer-data-option-field.component';
import { IsAnalysisTypePipe } from 'src/app/common/pipes/is-analysis-type.pipe';

describe('Designer Data Options', () => {
  let fixture: ComponentFixture<DesignerDataOptionFieldComponent>;
  let component: DesignerDataOptionFieldComponent;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, NoopAnimationsModule],
      declarations: [DesignerDataOptionFieldComponent, IsAnalysisTypePipe],
      providers: [
        {
          provide: Store,
          useValue: {}
        }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerDataOptionFieldComponent);
    component = fixture.componentInstance;
    component.artifactColumn = {} as any;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
