import * as toastr from 'toastr';
import { Injectable } from '@angular/core';
import { ErrorDetailDialogService } from './error-detail-dialog.service';

/* To make toasts stay indefinitely.
 * For testing purposes only

toastr.options.timeOut = 0;
toastr.options.extendedTimeOut = 0;
 */

@Injectable()
export class ToastService {
  constructor(private errorDetailDialog: ErrorDetailDialogService) {}
  success(msg, title = '', options = {}) {
    return toastr.success(msg, title, options);
  }

  info(msg, title = '', options = {}) {
    return toastr.info(msg, title, options);
  }

  warn(msg, title = '', options = {}) {
    return toastr.warning(msg, title, options);
  }

  error(msg, title = '', options: any = {}) {
    if (options.error) {
      const error = options.error;
      options.onclick = () => {
        this.errorDetailDialog.openErrorDetailDialog(error);
      };
      delete options.error;
    }
    return toastr.error(msg, title, options);
  }

  clear() {
    return toastr.clear();
  }
}
