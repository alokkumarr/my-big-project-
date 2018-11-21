import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { Location } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DesignerPageComponent } from './page.component';

class LocationStub {}

describe('DesignerPageComponent', () => {
  let component: DesignerPageComponent;
  let fixture: ComponentFixture<DesignerPageComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        declarations: [DesignerPageComponent],
        providers: [{ provide: Location, useValue: LocationStub }],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignerPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
