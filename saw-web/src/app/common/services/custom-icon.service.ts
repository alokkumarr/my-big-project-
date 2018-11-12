import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';

const basePath = '../../../../assets/img/category-icons/';

@Injectable()
export class CustomIconService {
  constructor(
    private _matIconRegistry: MatIconRegistry,
    private _domSanitizer: DomSanitizer
  ) {}

  public init() {
    this._registerCustomSVGIcons();
  }

  private _registerCustomSVGIcons() {
    const customSVGIcons = [
      ['calendar-events', `${basePath}calendar-events.svg`],
      ['calendar-funnel', `${basePath}calendar-funnel.svg`],
      ['calendar-retention', `${basePath}calendar-retention.svg`],
      ['category-default', `${basePath}category-default.svg`],
      ['category-errors', `${basePath}category-errors.svg`],
      ['category-orders', `${basePath}category-orders.svg`],
      ['category-sessions', `${basePath}category-sessions.svg`],
      ['category-subscribers', `${basePath}category-subscribers.svg`],
      ['category-usage', `${basePath}category-usage.svg`]
    ];
    forEach(customSVGIcons, ([name, path]) => {
      const safePath = this._domSanitizer.bypassSecurityTrustResourceUrl(path);
      this._matIconRegistry.addSvgIcon(name, safePath);
    });
  }
}
