import { TestBed } from '@angular/core/testing';

import { DesignerStateService } from './designer-state.service';

describe('DesignerStateService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DesignerStateService = TestBed.get(DesignerStateService);
    expect(service).toBeTruthy();
  });
});
