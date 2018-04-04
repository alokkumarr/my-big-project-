import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import * as map from 'lodash/map';

const template = require('./widget-category.component.html');
import { JwtService } from '../../../../../../login/services/jwt.service';

@Component({
  selector: 'widget-category',
  template
})

export class WidgetCategoryComponent implements OnInit {
  categories: any;
  @Output() onSelect = new EventEmitter();

  constructor(private jwt: JwtService) {
    this.categories = this.getCategories();
  }

  getCategories() {
    return map(
      this.jwt.getCategories(),
      cat => ({
        name: cat.prodModFeatureName,
        id: cat.prodModFeatureID,
        subCategories: map(
          cat.productModuleSubFeatures,
          subCat => ({
            name: subCat.prodModFeatureName,
            id: subCat.prodModFeatureID
          })
        )
      })
    );
  };

  ngOnInit() {
  }

  onSelectSubCategory(data) {
    this.onSelect.emit(data);
  }
}
