import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
const template = require('./analyze-new-dialog.component.html');

enum METHODS {
  ES_REPORT = 'table:esReport',
  REPORT = 'table:report',
  PIVOT = 'table:peport',
  CHART_COLUMN= 'chart:column',
  CHART_BAR = 'chart:bar',
  CHART_STACK = 'chart:stack',
  CHART_LINE = 'chart:line',
  CHART_DONUT = 'chart:donut',
  CHART_SCATTER = 'chart:scatter',
  CHART_BUBBLE = 'chart:bubble',
  CHART_AREA = 'chart:area',
  CHART_COMBO = 'chart:combo',
  CHART_TSSPLINE = 'chart:tsspline',
  CHART_TSPANE = 'chart:tsPane'
}

@Component({
  selector: 'analyze-new-dialog',
  template
})
export class AnalyzeNewDialogComponent implements OnInit {
  constructor(
    private _dialogRef: MatDialogRef<AnalyzeNewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: {
      metrics: any[],
      id: string
    }
  ) {}

  ngOnInit() { }
}
