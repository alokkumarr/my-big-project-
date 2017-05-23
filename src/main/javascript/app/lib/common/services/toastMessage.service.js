/**
 * Just a simple wrapper around the angular-toastr library
 * */
export function toastMessageService(toastr) {
  'ngInject';

  return {
    error
  };

  function error(msg) {
    toastr.error(msg);
  }
}
