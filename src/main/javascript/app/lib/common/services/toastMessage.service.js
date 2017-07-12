/**
 * Just a simple wrapper around the angular-toastr library
 * */
export function toastMessageService(toastr) {
  'ngInject';

  return {
    clear,
    error,
    info
  };

  function info(msg, title, options) {
    return toastr.info(msg, title, options);
  }

  function error(msg, title, options) {
    return toastr.error(msg, title, options);
  }

  function clear(toast) {
    return toastr.clear(toast);
  }
}
