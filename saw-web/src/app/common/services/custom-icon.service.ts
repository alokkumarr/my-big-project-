import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { Injectable } from '@angular/core';

const basePath = '../../../../assets/img/category-icons/';

@Injectable()
export class CustomIconService {
  constructor(
    private _matIconRegistry: MatIconRegistry,
    private _domSanitizer: DomSanitizer
  ) {}

  public init() {
    this._registerCustomSVGIconSet();
  }

  private _registerCustomSVGIconSet() {
    const path = `${basePath}category-icon-set.svg`;
    const safePath = this._domSanitizer.bypassSecurityTrustResourceUrl(path);
    this._matIconRegistry.addSvgIconSet(safePath);
  }
}
