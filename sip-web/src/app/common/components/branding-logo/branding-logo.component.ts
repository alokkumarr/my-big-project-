import { Component, OnInit, Input } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { BrandingService } from './../../../modules/admin/branding/branding.service';

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
  public BrandLogoBinary = '';

  ngOnInit() {
    this._brandingService.getBrandingDetails().subscribe(data => {
      this.BrandLogoBinary = isEmpty(data.brandImage)  ? '' : 'data:image/gif;base64,' + data.brandImage;
    });
  }
}
