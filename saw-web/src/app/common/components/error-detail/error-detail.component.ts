import {
  Component,
  Inject,
  OnInit
} from '@angular/core';
import {
  MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material';

import { ErrorDetailService } from '../../services/error-detail.service';

const style = require('./error-detail.component.scss');

@Component({
  selector: 'error-detail',
  templateUrl: './error-detail.component.html',
  styles: [style]
})
export class ErrorDetailComponent implements OnInit {
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
