import { ShowPasswordDirective } from './show-password.directive';
import { Component, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TestBed, async, ComponentFixture } from '@angular/core/testing';

@Component({
  selector: 'test-directive',
  template: `
    <div>
      <div>
        <input showPassword />
      </div>

      <div class="mat-form-field-suffix">
        <mat-icon></mat-icon>
      </div>
    </div>
  `
})
class TestDirectiveComponent {}

describe('ShowPasswordDirective', () => {
  let component: TestDirectiveComponent;
  let fixture: ComponentFixture<TestDirectiveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TestDirectiveComponent, ShowPasswordDirective],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TestDirectiveComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create an instance', () => {
    expect(component).toBeTruthy();
  });
});
