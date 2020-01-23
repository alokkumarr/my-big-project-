import { Component, OnInit, Input } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { BrandingService } from './../../../modules/admin/branding/branding.service';
import { DEFAULT_BRANDING_COLOR } from './../../../common/consts';

@Component({
  selector: 'app-branding-logo',
  templateUrl: './branding-logo.component.html',
  styleUrls: ['./branding-logo.component.scss']
})
export class BrandingLogoComponent implements OnInit {
  constructor(
    public _brandingService: BrandingService
  ) {}

  @Input() page: any;
  public brandLogoBinary = '';

  ngOnInit() {
    this._brandingService.getBrandingDetails().subscribe(data => {
      this.brandLogoBinary = isEmpty(data.brandImage)  ? '' : 'data:image/gif;base64,' + data.brandImage;
      const brandingColor = isEmpty(data.brandColor) ? DEFAULT_BRANDING_COLOR : data.brandColor;
      this._brandingService.savePrimaryColor(brandingColor);
    });
  }
}
