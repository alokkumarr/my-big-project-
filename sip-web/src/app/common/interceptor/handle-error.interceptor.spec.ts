import { HandleErrorInterceptor } from './handle-error.interceptor';
import { ToastService } from '../services';
import { throwError } from 'rxjs';

const nextHandler = { handle: () => throwError(new Error('Test Error')) };
// const noop = () => {};

describe('Handle error inteceptor', () => {
  it('should get error title', () => {
    const toast: ToastService = null;
    const interceptor = new HandleErrorInterceptor(toast);
    expect(interceptor.getTitle({ message: 'abc' })).toEqual('abc');
  });

  it('should not show toast for refresh token endpoint', () => {
    const toast: ToastService = { error: () => {} } as any;
    const spy = spyOn(toast, 'error').and.returnValue(true);

    const interceptor = new HandleErrorInterceptor(toast);
    interceptor.intercept(
      { url: 'getNewAccessToken', headers: { has: () => false } } as any,
      nextHandler as any
    );
    expect(spy).not.toHaveBeenCalled();
  });
});
