import {
  Component,
  Input,
  Inject
} from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogConfig
} from '@angular/material';

import { ErrorDetailService } from '../../services/error-detail.service';

require('./error-detail.component.scss');
const template = require('./error-detail.component.html');

const ERROR_TITLE_LENGTH = 50;

@Component({
  selector: 'error-detail',
  template
})
export class ErrorDetailComponent {
  public errorMessage: string;
  public errorBody: any;
  constructor(
    private _dialogRef: MatDialogRef<ErrorDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {errorObj},
    private _errorDetailService: ErrorDetailService
  ) { }

  ngOnInit() {
    this.errorMessage = this._errorDetailService.getTitle(this.data.errorObj);
    this.errorBody = this._errorDetailService.getDetail(this.data.errorObj);
  }


  cancel() {
    this._dialogRef.close();
  }
}
