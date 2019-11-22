import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AccordionMenuLinkComponent } from './accordionMenuLink.component';
import { MaterialModule } from 'src/app/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SidenavComponent } from '../sidenav';
import {
  ConfigService
} from '../../services/configuration.service';

const mockService = {};

const metaDataStub = {
  id: 2,
  name: 'CANNED ANALYSIS',
  children: []
};

describe('Accordoin menu link Component', () => {
  let fixture: ComponentFixture<AccordionMenuLinkComponent>;
  let component: AccordionMenuLinkComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: ConfigService, useValue: mockService },
        { provide: SidenavComponent, useValue: mockService }
      ],
      imports: [MaterialModule, FormsModule, BrowserAnimationsModule, RouterTestingModule],
      declarations: [AccordionMenuLinkComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccordionMenuLinkComponent);
    component = fixture.componentInstance;
    component.metadata = metaDataStub;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });

  it('should check panel of the menu', () => {
    const checkingPanelBasedOnChild = fixture.componentInstance.checkPanel();
    expect(checkingPanelBasedOnChild).toBeFalsy();
  });

  it('should check if children are present', () => {
    const checkingHasChildren = fixture.componentInstance.hasChildren();
    expect(checkingHasChildren).toBeFalsy();
  });
});
