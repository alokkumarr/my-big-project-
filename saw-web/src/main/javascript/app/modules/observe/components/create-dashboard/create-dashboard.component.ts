import { Component, Inject, ViewChild } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA, MdDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';

import { AnalysisChoiceComponent } from '../analysis-choice/analysis-choice.component';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

@Component({
  selector: 'create-dashboard',
  template
})
export class CreateDashboardComponent {
  @ViewChild('gridster') gridster: GridsterComponent;

  public loadedAnalyses = [];
  public analyses = [];
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem>;
  public chartUpdater = new BehaviorSubject({});
  public chartOptions = { 'chart': { 'type': 'pie', 'spacingLeft': 45, 'spacingRight': 45, 'spacingBottom': 45, 'spacingTop': 45, 'height': 580, 'reflow': true }, 'legend': { 'align': 'right', 'verticalAlign': 'middle', 'layout': 'vertical', 'enabled': false, 'itemMarginTop': 5, 'maxHeight': 200, 'itemMarginBottom': 5, 'itemStyle': { 'lineHeight': '14px' } }, 'series': [{ 'name': 'Brands', 'colorByPoint': true, 'data': [] }], 'plotOptions': { 'pie': { 'showInLegend': false }, 'series': { 'barBgColor': '#f3f5f8', 'marker': { 'fillColor': null, 'lineWidth': 2, 'lineColor': null } } }, 'colors': ['#0375bf', '#4c9fd2', '#bfdcef', '#490094', '#9A72C4', '#C8B2DF', '#006ADE', '#6AB4FF', '#B5DAFF', '#014752', '#009293', '#73C3C4', '#4CEA7C', '#9DF4B7', '#C9F9D8', '#DD5400', '#EDA173', '#F5CDB4', '#940000', '#C47373', '#DFB2B2'], 'exporting': { 'enabled': false }, 'tooltip': { 'useHTML': true, 'valueDecimals': 2, 'headerFormat': '<span style="font-size: 12px; opacity: 0.8;">{point.key}</span><br/>', 'pointFormat': '<span style="color:{point.color}; stroke: white; stroke-width: 2; font-size: 25px;">●</span> {series.name}: <b>{point.y}</b><br/>' }, 'title': { 'text': '' }, 'lang': { 'noData': 'No data to display' }, 'noData': { 'style': { 'fontWeight': 'bold', 'fontSize': '15px', 'color': '#303030' } }, 'credits': false };

  constructor(public dialogRef: MdDialogRef<CreateDashboardComponent>,
    public dialog: MdDialog,
    @Inject(MD_DIALOG_DATA) public layout: any) {
  }

  static itemChange(item, itemComponent) {
    setTimeout(() => {
      item.updater.next([]);
    }, 500)
  }

  ngOnInit() {
    this.options = {
      gridType: 'scrollVertical',
      minCols: 2,
      maxCols: 100,
      minRows: 2,
      maxRows: 100,
      itemChangeCallback: CreateDashboardComponent.itemChange,
      draggable: {
        enabled: true
      },
      resizable: {
        enabled: true
      }
    };

    this.dashboard = [];
  }

  addAnalysis(index, analysis) {
    if (!Array.isArray(this.analyses[index])) {
      this.analyses[index] = [];
    }
    this.analyses[index].push(analysis);
    return analysis;
  }

  removeAnalysis(index, analysis) {
    if (!Array.isArray(this.analyses[index])) {
      this.analyses[index] = [];
    }

    this.analyses[index] = this.analyses[index].filter(an => an.id !== analysis.id);
    return analysis;
  }

  removeTile(item: GridsterItem) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
  }

  hasAnalyses(index) {
    if (!Array.isArray(this.analyses[index])) {
      this.analyses[index] = [];
    }

    return this.analyses[index].length > 0;
  }

  exitCreator(data) {
    this.dialogRef.close(data);
  }

  chooseAnalysis() {
    const dialogRef = this.dialog.open(AnalysisChoiceComponent);

    dialogRef.afterClosed().subscribe(analysis => {
      if (!analysis) {
        return;
      }

      const item = { cols: 1, rows: 1, analysis, updater: new BehaviorSubject({}) };
      this.dashboard.push(item);
      item.updater.next([{ 'path': 'series', 'data': [{ 'name': 'Total Failed Bytes', 'data': [{ 'name': 'ANDROID', 'y': 31738882905685, 'drilldown': 'ANDROID' }, { 'name': 'UNKNOWN', 'y': 18794631884548, 'drilldown': 'UNKNOWN' }, { 'name': 'IOS', 'y': 30719183764427, 'drilldown': 'IOS' }, { 'name': 'WP8.1', 'y': 18555545758, 'drilldown': 'WP8.1' }, { 'name': 'BLACKBERRY', 'y': 17692721302, 'drilldown': 'BLACKBERRY' }], 'dataLabels': { 'enabled': true } }] }]);
    });
  }

  onDrop({ dragData }, destinationIndex) {
    const { sourceIndex, analysis } = dragData;
    if (sourceIndex === destinationIndex) {
      return analysis;
    }

    // if (this.hasAnalyses(destinationIndex)) {
    //   const replaceAnalysis = this.analyses[destinationIndex][0];
    //   this.addAnalysis(sourceIndex, replaceAnalysis);
    //   this.removeAnalysis(destinationIndex, replaceAnalysis);
    // }

    this.addAnalysis(destinationIndex, analysis);
    this.removeAnalysis(sourceIndex, analysis);
  }
}
