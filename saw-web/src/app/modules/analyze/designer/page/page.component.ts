import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AnalyzeService } from '../../services/analyze.service';
import { DesignerMode } from '../../types';

interface NewDesignerQueryParams {
  mode: DesignerMode;
  categoryId: string;
  metricName: string;
  semanticId: string;
  type: string;
  chartType?: string;
}

interface ExistingDesignerQueryParams {
  mode: DesignerMode;
  analysisId: string;
}

type DesignerQueryParams = NewDesignerQueryParams | ExistingDesignerQueryParams;

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
    private location: Location,
    private analyzeService: AnalyzeService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.convertParamsToData(this.route.snapshot
      .queryParams as DesignerQueryParams);
  }

  onBack() {
    this.location.back();
  }

  onSave() {
    this.location.back();
  }

  convertParamsToData(params: DesignerQueryParams) {
    this.designerMode = params.mode;

    /* If new analysis, setup a basic analysis starter */
    if (params.mode === 'new') {
      const newAnalysisParams = <NewDesignerQueryParams>params;
      this.analysisStarter = {
        categoryId: newAnalysisParams.categoryId,
        metricName: newAnalysisParams.metricName,
        semanticId: newAnalysisParams.semanticId,
        type: newAnalysisParams.type,
        chartType: newAnalysisParams.chartType,
        name: 'Untitled Analysis',
        description: '',
        scheduled: null
      };

      /* Else, load existing analysis */
    } else {
      const existingAnalysisParams = <ExistingDesignerQueryParams>params;
      this.analyzeService
        .readAnalysis(existingAnalysisParams.analysisId)
        .then(analysis => {
          this.analysis = analysis;
        });
    }
  }
}
