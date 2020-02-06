import { TestBed, async } from '@angular/core/testing';
import { JwtService } from './jwt.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';

// tslint:disable
const validToken =
  'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXdhZG1pbkBzeW5jaHJvbm9zcy5jb20iLCJ0aWNrZXQiOnsidGlja2V0SWQiOiIzOF8xNTc5NDIzNzc4NDUxXzMxNjI5NjQ2MyIsIndpbmRvd0lkIjoiMzhfMTU3OTQyMzc3ODQ1MV8xNjk2NDA4NzQyIiwibWFzdGVyTG9naW5JZCI6InNhd2FkbWluQHN5bmNocm9ub3NzLmNvbSIsInVzZXJGdWxsTmFtZSI6InN5c3RlbSBzbmNyIGFkbWluIiwiZGVmYXVsdFByb2RJRCI6IjQiLCJyb2xlQ29kZSI6IlNZTkNIUk9OT1NTX0FETUlOX1VTRVIiLCJyb2xlVHlwZSI6IkFETUlOIiwiY3JlYXRlZFRpbWUiOjE1Nzk0MjM3Nzg0NTIsImVycm9yIjpudWxsLCJjdXN0SUQiOiIxIiwiY3VzdENvZGUiOiJTWU5DSFJPTk9TUyIsImlzSnZDdXN0b21lciI6MSwiZmlsdGVyQnlDdXN0b21lckNvZGUiOjAsInVzZXJJZCI6MSwidmFsaWRVcHRvIjo5OTk5OTk5OTk5OTk5LCJ2YWxpZCI6dHJ1ZSwidmFsaWRpdHlSZWFzb24iOiJVc2VyIEF1dGhlbnRpY2F0ZWQgU3VjY2Vzc2Z1bGx5IiwidmFsaWRNaW5zIjpudWxsLCJzaXBEc2tBdHRyaWJ1dGUiOm51bGwsImN1c3RvbUNvbmZpZyI6WyJlcy1hbmFseXNpcy1hdXRvLXJlZnJlc2giXX0sImlhdCI6MTU3OTQyMzc3OH0.ijSCkLTJIfBSeiL07YQIkscxbqHoC6WjwOBtNsg42Jg';
const inValidToken =
  'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXdhZG1pbkBzeW5jaHJvbm9zcy5jb20iLCJ0aWNrZXQiOnsidGlja2V0SWQiOiIzOF8xNTc5NDIzNzc4NDUxXzMxNjI5NjQ2MyIsIndpbmRvd0lkIjoiMzhfMTU3OTQyMzc3ODQ1MV8xNjk2NDA4NzQyIiwibWFzdGVyTG9naW5JZCI6InNhd2FkbWluQHN5bmNocm9ub3NzLmNvbSIsInVzZXJGdWxsTmFtZSI6InN5c3RlbSBzbmNyIGFkbWluIiwiZGVmYXVsdFByb2RJRCI6IjQiLCJyb2xlQ29kZSI6IlNZTkNIUk9OT1NTX0FETUlOX1VTRVIiLCJyb2xlVHlwZSI6IkFETUlOIiwiY3JlYXRlZFRpbWUiOjE1Nzk0MjM3Nzg0NTIsImVycm9yIjpudWxsLCJjdXN0SUQiOiIxIiwiY3VzdENvZGUiOiJTWU5DSFJPTk9TUyIsImlzSnZDdXN0b21lciI6MSwiZmlsdGVyQnlDdXN0b21lckNvZGUiOjAsInVzZXJJZCI6MSwidmFsaWRVcHRvIjoxNTc5NDMwOTc4NDUyLCJ2YWxpZCI6ZmFsc2UsInZhbGlkaXR5UmVhc29uIjoiVXNlciBBdXRoZW50aWNhdGVkIFN1Y2Nlc3NmdWxseSIsInZhbGlkTWlucyI6bnVsbCwic2lwRHNrQXR0cmlidXRlIjpudWxsLCJjdXN0b21Db25maWciOlsiZXMtYW5hbHlzaXMtYXV0by1yZWZyZXNoIl19LCJpYXQiOjE1Nzk0MjM3Nzh9.B0e0utOdGQJqVT1Bp6poCJg5OS7sUPk13ksoRujieOQ';
// tslint:enable

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

<<<<<<< HEAD
  it('should parse jwt and validate it', () => {
    const validJwt = jwtService.parseJWT(validToken);
    const inValidJwt = jwtService.parseJWT(inValidToken);
    expect(jwtService.isValid(validJwt)).toEqual(true);
    expect(jwtService.isValid(inValidJwt)).toEqual(false);
=======
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
>>>>>>> master
  });
});
