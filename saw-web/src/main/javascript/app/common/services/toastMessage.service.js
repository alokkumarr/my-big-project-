import * as toastr from 'toastr';

/**
 * Just a simple wrapper around the angular-toastr library
 * */
export class ToastService {
  success(msg, title, options) {
    return toastr.success(msg, title, options);
  }

  info(msg, title, options) {
    return toastr.info(msg, title, options);
  }

  warn(msg, title, options) {
    return toastr.warning(msg, title, options);
  }

  /* For testing purposes
   // TODO rollback after e2e for pivot done
   error(msg, title, options) {
   return this._toastr.error(msg, title, {
   ...options,
   timeOut: 5 * 10 * 1000,
   extendedTimeOut: 5 * 10 * 1000
   });
   } */

  error(msg, title, options) {
    return toastr.error(msg, title, options);
  }

  clear(toast) {
    return toastr.clear(toast);
  }
}
