import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'admin-import-category-select',
  templateUrl: './category-select.component.html',
  styleUrls: ['./category-select.component.scss']
})
export class AdminImportCategorySelectComponent implements OnInit {
  @Input() categories: any[];
  @Input() value: any;
  @Output() change: EventEmitter<string> = new EventEmitter();
  constructor() {}

  ngOnInit() {}

  onCategoryChange(categoryId: string) {
    this.change.emit(categoryId);
  }
}
