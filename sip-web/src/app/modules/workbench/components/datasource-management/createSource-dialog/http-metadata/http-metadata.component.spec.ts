import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpMetadataComponent } from './http-metadata.component';
import { FormsModule, FormBuilder } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { E2eDirective } from 'src/app/common/directives';

describe('HttpMetadataComponent', () => {
  let component: HttpMetadataComponent;
  let fixture: ComponentFixture<HttpMetadataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HttpMetadataComponent, E2eDirective],
      imports: [FormsModule, MaterialModule, NoopAnimationsModule]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HttpMetadataComponent);
    component = fixture.componentInstance;

    const formBuilder: FormBuilder = TestBed.get(FormBuilder);
    component.parentForm = formBuilder.group({
      httpMethod: '',
      apiEndPoint: '',
      headerParameters: formBuilder.array([]),
      queryParameters: formBuilder.array([]),
      bodyParameters: formBuilder.group({
        content: ''
      })
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
