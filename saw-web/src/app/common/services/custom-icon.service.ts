import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { Injectable } from '@angular/core';

const basePath = '/assets/img/';

@Injectable()
export class CustomIconService {
  constructor(
    private _matIconRegistry: MatIconRegistry,
    private _domSanitizer: DomSanitizer
  ) {}

  public init() {
    this._registerCustomSVGIconSet('category-icons/category-icon-set.svg');
    this._registerCustomSVGIconSet(
      'colored-chart-icons/colored-chart-icon-set.svg'
    );
    this._registerCustomSVGIconSet('module-icons/module-icons-set.svg');
  }

  private _registerCustomSVGIconSet(path) {
    const safePath = this._domSanitizer.bypassSecurityTrustResourceUrl(
      `${basePath}${path}`
    );
    this._matIconRegistry.addSvgIconSet(safePath);
  }
}
