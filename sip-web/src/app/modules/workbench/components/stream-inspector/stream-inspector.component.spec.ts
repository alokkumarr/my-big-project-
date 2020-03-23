import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { StreamInspectorComponent } from './stream-inspector.component';
import { WorkbenchService } from '../../services/workbench.service';
import { of } from 'rxjs';

class WorkbenchServiceStub {
  getListOfStreams() {
    return of([]);
  }
}

describe('StreamInspector Component', () => {
  let component: StreamInspectorComponent;
  let fixture: ComponentFixture<StreamInspectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: WorkbenchService, useClass: WorkbenchServiceStub }
      ],

      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [StreamInspectorComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StreamInspectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
