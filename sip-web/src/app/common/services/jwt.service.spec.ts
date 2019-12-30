import { TestBed, async } from '@angular/core/testing';
import { JwtService } from './jwt.service';

describe('JWT Service', () => {
  let jwtService: JwtService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [JwtService]
    }).compileComponents();
  }));

  beforeEach(() => {
    jwtService = TestBed.get(JwtService);
  });

  it('should validate token correctly', () => {
    expect(
      jwtService.isValid({
        ticket: { valid: true, validUpto: Date.now() + 5000 }
      })
    ).toEqual(true);
  });

  it('should return validity message if present', () => {
    const reason = 'abc';
    expect(
      jwtService.getValidityReason({ ticket: { validityReason: reason } })
    ).toEqual(reason);
  });

  it('should return product id', () => {
    expect(jwtService.productId).toEqual('');
  });

  it('should return customer id', () => {
    expect(jwtService.customerId).toEqual('');
  });
});
