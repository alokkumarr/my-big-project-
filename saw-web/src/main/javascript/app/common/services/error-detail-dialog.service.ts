import { Injectable } from '@angular/core';
import {
  MatDialog,
  MatDialogConfig
} from '@angular/material';

import { ErrorDetailComponent } from '../components/error-detail';

@Injectable()
export class ErrorDetailDialogService {

  constructor(public dialog: MatDialog) {}

  openErrorDetailDialog(errorObj) {
    return this.dialog.open(ErrorDetailComponent, {
      width: 'auto',
      height: 'auto',
      data: { errorObj }
    } as MatDialogConfig);
  }
}
