import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import { AggregateChooserComponent } from './aggregate-chooser.component';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('Aggregate Chooser Component', () => {
  let component: AggregateChooserComponent;
  let fixture: ComponentFixture<AggregateChooserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AggregateChooserComponent],
      imports: [MaterialModule, NoopAnimationsModule],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AggregateChooserComponent);
    component = fixture.componentInstance;
  });

  it('should be initialised', () => {
    expect(component).toBeTruthy();
  });
});
