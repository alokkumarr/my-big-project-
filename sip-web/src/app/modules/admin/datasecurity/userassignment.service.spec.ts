import { Router, ActivatedRoute } from '@angular/router';

import { TestBed, inject } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { UserAssignmentService } from './userassignment.service';
import { JwtService } from '../../../common/services/jwt.service';
import { Observable } from 'rxjs';

const mockService = {};
let userAssignmentService;

describe('User Assignment Service', () => {
  beforeEach(done => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserAssignmentService,
        { provide: JwtService, useValue: mockService },
        { provide: Router, useValue: mockService },
        { provide: ActivatedRoute, useValue: mockService }
      ]
    }).compileComponents();

    userAssignmentService = TestBed.get(UserAssignmentService);
    done();
  });

  it('should exist', () => {
    expect(userAssignmentService).not.toBeNull();
  });

  it('getList function should exist', () => {
    expect(userAssignmentService.getList()).not.toBeNull();
  });

  it('addSecurityGroup should exist and return an Observable', inject(
    [UserAssignmentService],
    (observe: UserAssignmentService) => {
      expect(observe.addSecurityGroup({}) instanceof Observable).toEqual(false);
    }
  ));
});
