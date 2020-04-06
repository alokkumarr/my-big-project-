import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { StreamReaderGridComponent } from './stream-reader-grid.component';

describe('StreamInspector Component', () => {
  let component: StreamReaderGridComponent;
  let fixture: ComponentFixture<StreamReaderGridComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [],

      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [StreamReaderGridComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StreamReaderGridComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(component).toBeTruthy();
  });
});
