import { TestBed, async } from '@angular/core/testing';
import { JwtService } from './jwt.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('JWT Service', () => {
  let jwtService: JwtService;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
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

  describe('parseJWT', () => {
    it('should handle no input', () => {
      expect(jwtService.parseJWT(null)).toBeFalsy();
    });

    it('should parse json object correctly', () => {
      const a = { test: 'value' };
      const parsedJson = jwtService.parseJWT(`abc.${btoa(JSON.stringify(a))}`);
      expect(parsedJson.test).toEqual(a.test);
    });
  });

  describe('getTokenObj', () => {
    it('should return null if not object set', () => {
      const spy = spyOn(jwtService, 'get').and.returnValue(null);
      expect(jwtService.getTokenObj()).toBeFalsy();
      expect(spy).toHaveBeenCalled();
    });
  });

  it('should return token productID', () => {
    const spy = spyOn(jwtService, 'getTokenObj').and.returnValue({
      ticket: { defaultProdID: 'abc' }
    });
    expect(jwtService.productId).toEqual('abc');
    expect(spy).toHaveBeenCalled();
  });

  it('should return login id', () => {
    const spy = spyOn(jwtService, 'getTokenObj').and.returnValue({
      ticket: { masterLoginId: 'abc' }
    });
    expect(jwtService.getLoginId()).toEqual('abc');
    expect(spy).toHaveBeenCalled();
  });
});
