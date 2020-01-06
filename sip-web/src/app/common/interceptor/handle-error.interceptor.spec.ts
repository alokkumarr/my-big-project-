import { HandleErrorInterceptor } from './handle-error.interceptor';
import { ToastService } from '../services';

describe('Handle error inteceptor', () => {
  it('should get error title', () => {
    const toast: ToastService = null;
    const interceptor = new HandleErrorInterceptor(toast);
    expect(interceptor.getTitle({ message: 'abc' })).toEqual('abc');
  });
});
