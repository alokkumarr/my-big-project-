import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import * as startsWith from 'lodash/startsWith';
import * as replace from 'lodash/replace';

import { STAGING_TREE } from '../../../wb-comp-configs';
import { IFileSystemAPI, ISelectionEvent } from '../../../../../common/components/remote-folder-selector';
import { WorkbenchService } from '../../../services/workbench.service';

@Component({
  selector: 'select-folder-dialog',
  templateUrl: './select-folder-dialog.component.html'
})
export class SourceFolderDialogComponent {
  public selectedPath = '';
  public rootNode = STAGING_TREE;
  public fileSystemAPI: IFileSystemAPI;
  constructor(
    private dialogRef: MatDialogRef<SourceFolderDialogComponent>,
    public workBench: WorkbenchService,
    ) {
    this.fileSystemAPI = {
      getDir: this.workBench.getStagingData,
      createDir: this.workBench.createFolder
    };
  }

  onFolderSelected({folder}: ISelectionEvent) {
    switch (folder.path) {
    case 'root':
      this.selectedPath = `/`;
      break;
    case '/':
      this.selectedPath = `/${folder.name}`;
      break;
    default:
    this.selectedPath = `${folder.path}/${folder.name}`;
      break;
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onYesClick(): void {
    const result = startsWith(this.selectedPath, '\\') ?
      replace(this.selectedPath, '/', '') : this.selectedPath;

    this.dialogRef.close(result);
  }
}
