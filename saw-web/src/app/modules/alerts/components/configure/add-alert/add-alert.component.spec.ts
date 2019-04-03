import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MaterialModule } from '../../../../../material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { AddAlertComponent } from './add-alert.component';

describe('AddAlertComponent', () => {
  let component: AddAlertComponent;
  let fixture: ComponentFixture<AddAlertComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AddAlertComponent],
      imports: [
        MaterialModule,
        ReactiveFormsModule,
        FormsModule,
        NoopAnimationsModule
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
