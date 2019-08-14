import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpMetadataComponent } from './http-metadata.component';

describe('HttpMetadataComponent', () => {
  let component: HttpMetadataComponent;
  let fixture: ComponentFixture<HttpMetadataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HttpMetadataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HttpMetadataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
