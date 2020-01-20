import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed, async } from '@angular/core/testing';
import { Store } from '@ngxs/store';

import { UserService } from './user.service';
import { JwtService } from './jwt.service';

// tslint:disable
const responseWithValidToken = {
  aToken: 'isValid',
  rToken: '',
  validity: true,
  message: 'User Authenticated Successfully'
};
const responseWithInValidToken = {
  aToken: 'isInValid',
  rToken: '',
  validity: true,
  message: 'User Authenticated Successfully'
};
// tslint:enable

class JwtServiceStub {
  parseJWT(token: string) {
    const isValid = token.includes('isValid');
    return { valid: isValid };
  }

  isValid(token) {
    return token.valid;
  }

  set(accessToken, refreshToken) {}
}
class StoreStub {
  dispatch() {}
}

describe('User Service', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserService,
        { provide: Store, useClass: StoreStub },
        { provide: JwtService, useClass: JwtServiceStub }
      ]
    }).compileComponents();
  }));

  it('should set jwt token when the token is valid', () => {
    const jwtService: JwtService = TestBed.get(JwtService);
    const userService: UserService = TestBed.get(UserService);
    const spy = spyOn(jwtService, 'set');
    userService.saveJWTInLocalStorageIfValid(responseWithValidToken);
    expect(spy).toHaveBeenCalled();
  });

  it('should not set jwt token when the token is not valid', () => {
    const jwtService: JwtService = TestBed.get(JwtService);
    const userService: UserService = TestBed.get(UserService);
    const spy = spyOn(jwtService, 'set');
    userService.saveJWTInLocalStorageIfValid(responseWithInValidToken);
    expect(spy).not.toHaveBeenCalled();
  });
});
