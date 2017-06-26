/**
 * Just a simple wrapper around the angular-toastr library
 * */
export function toastMessageService(toastr) {
  'ngInject';

  return {
    error,
    info
  };

  function info(msg) {
    toastr.info(msg);
  }

  function error(msg, title, options) {
    toastr.error(msg, title, options);
  }
}
