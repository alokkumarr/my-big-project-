import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'alias-rename-dialog',
  templateUrl: './alias-rename-dialog.component.html',
  styleUrls: ['./alias-rename-dialog.component.scss']
})
export class AliasRenameDialogComponent {
  public aliasName: string;

  constructor(
    public _dialogRef: MatDialogRef<AliasRenameDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      aliasName: string;
    }
  ) {
    this.aliasName = this.data.aliasName;
  }

  onAliasChange(aliasName) {
    this.aliasName = aliasName;
  }

  close() {
    this._dialogRef.close();
  }

  rename() {
    this._dialogRef.close(this.aliasName);
  }
}
