/**
 * Just a simple wrapper around the angular-toastr library
 * */
export class ToastService {
  constructor(toastr) {
    this._toastr = toastr;
  }

  success(msg, title, options) {
    return this._toastr.success(msg, title, options);
  }

  info(msg, title, options) {
    return this._toastr.info(msg, title, options);
  }

  warn(msg, title, options) {
    return this._toastr.warning(msg, title, options);
  }

  error(msg, title, options) {
    return this._toastr.error(msg, title, options);
  }

  clear(toast) {
    return this._toastr.clear(toast);
  }
}
