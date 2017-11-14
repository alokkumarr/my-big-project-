import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import DesignerService from '../designer.service';
import Analysis from '../../../models/analysis.model';
import {
  DesignerMode,
  AnalysisType
} from '../../../constsTS';
const template = require('./designer-container.component.html');
require('./designer-container.component.scss');

@Component({
  selector: 'designer-container',
  template
})
export default class DesignerContainerComponent {
  @Input() public analysis: Analysis;
  @Input() public designerMode: DesignerMode;
  @Output() public onBack: EventEmitter<any> = new EventEmitter();
  public isInDraftMode: boolean = false;

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    const type = this.analysis.type;
    const semanticId = this.analysis.semanticId;
    console.log('mode: ', this.designerMode);
    console.log(`onInit: ${type} - ${semanticId}`);
    console.log('analysis: ', this.analysis);
    this._designerService.createAnalysis(semanticId, type);
  }

  onSave() {
    console.log('save');
  }
}
