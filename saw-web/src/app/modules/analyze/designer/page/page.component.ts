import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { DesignerStateService } from '../designer-state.service';
import * as cloneDeep from 'lodash/cloneDeep';

@Component({
  selector: 'designer-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.scss']
})
export class DesignerPageComponent implements OnInit {
  analysis: any;
  analysisStarter: any;
  designerMode: string;

  constructor(
    private designerStateService: DesignerStateService,
    private location: Location
  ) {}

  ngOnInit() {
    this.analysis = cloneDeep(this.designerStateService.analysis);
    this.analysisStarter = cloneDeep(this.designerStateService.analysisStarter);
    this.designerMode = this.designerStateService.designerMode;
  }

  onBack() {
    this.location.back();
  }

  onSave() {
    this.location.back();
  }
}
