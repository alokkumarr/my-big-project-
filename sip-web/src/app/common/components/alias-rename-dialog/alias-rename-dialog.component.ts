import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'alias-rename-dialog',
  templateUrl: './alias-rename-dialog.component.html',
  styleUrls: ['./alias-rename-dialog.component.scss']
})
export class AliasRenameDialogComponent {
  public alias: string;

  constructor(
    public _dialogRef: MatDialogRef<AliasRenameDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      alias: string;
    }
  ) {
    this.alias = this.data.alias;
  }

  onAliasChange(alias) {
    this.alias = alias;
  }

  close() {
    this._dialogRef.close();
  }

  rename() {
    this._dialogRef.close(this.alias);
  }
}
