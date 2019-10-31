import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PasswordToggleComponent } from './password-toggle.component';
import { MaterialModule } from 'src/app/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

const showPasswordStub = false;

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<PasswordToggleComponent>;
  let component: PasswordToggleComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [],
      imports: [MaterialModule, FormsModule, BrowserAnimationsModule],
      declarations: [PasswordToggleComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PasswordToggleComponent);
    component = fixture.componentInstance;
    component.showPassword = showPasswordStub;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });
});
