import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'stream-reader-grid',
  templateUrl: './stream-reader-grid.component.html',
  styleUrls: ['./stream-reader-grid.component.scss']
})
export class StreamReaderGridComponent implements OnInit {
  public gridData;

  @Input('gridData') set setGridData(data) {
    if (data) {
      this.gridData = data;
    }
  }
  constructor() {}

  ngOnInit() {}
}
