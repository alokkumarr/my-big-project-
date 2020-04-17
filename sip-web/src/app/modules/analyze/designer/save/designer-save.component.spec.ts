import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import { DesignerSaveComponent } from '.';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/app/material.module';
import { JwtService } from 'src/app/common/services';
import { AnalyzeService } from '../../services/analyze.service';

describe('DesignerSaveComponent', () => {
  let fixture: ComponentFixture<DesignerSaveComponent>;
  let component: DesignerSaveComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DesignerSaveComponent],
      imports: [ReactiveFormsModule, MaterialModule, NoopAnimationsModule],
      providers: [
        { provide: JwtService, useValue: {} },
        { provide: AnalyzeService, useValue: {} }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerSaveComponent);
    component = fixture.componentInstance;
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
