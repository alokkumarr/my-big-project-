import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { AnalyzeDialogService } from '../../../services/analyze-dialog.service';
import { Artifact, AnalysisDSL } from '../../types';

@Component({
  selector: 'designer-settings-query',
  templateUrl: './designer-settings-query.component.html',
  styleUrls: ['./designer-settings-query.component.scss']
})
export class DesignerSettingsQueryComponent implements OnInit {
  @Output() save = new EventEmitter<any>();
  @Output() change = new EventEmitter<string>();
  @Output() submit = new EventEmitter<any>();
  @Input() analysis: AnalysisDSL;
  @Input() artifacts: Artifact[];
  constructor(private _analyzeDialogService: AnalyzeDialogService) {}

  ngOnInit() {}

  onQueryChange(query: string) {
    this.change.emit(query);
  }

  doSubmit() {
    this.save.emit();
  }

  submitQuery() {
    if (!this.analysis.designerEdit) {
      this._analyzeDialogService
        .openQueryConfirmationDialog()
        .afterClosed()
        .subscribe(result => {
          if (result) {
            this.doSubmit();
          }
        });
    } else {
      this.doSubmit();
    }
  }
}
