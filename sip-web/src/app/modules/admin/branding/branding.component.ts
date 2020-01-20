import { Component } from '@angular/core';
import * as get from 'lodash/get';
import APP_CONFIG from '../../../../../appConfig';
import { BrandingService } from './branding.service';
import { ToastService } from '../../../common/services';

@Component({
  selector: 'admin-branding',
  templateUrl: './branding.component.html',
  styleUrls: ['./branding.component.scss']
})
export class AdminBrandingComponent {
  primaryColor = '#bb0000';
  filesToUpload: any;
  binaryFormatImage: any;
  public api = get(APP_CONFIG, 'api.url');

  constructor(
    private _brandingService: BrandingService,
    public _toastMessage: ToastService
  ) {}

  ngOnInit(): void {
    this._brandingService.savePrimaryColor(this.primaryColor);
  }

  prepareBrandimgForUpload(event) {
    this.filesToUpload = event.srcElement.files;
    this.binaryFormatImage = this.filesToUpload[0];
  }

  saveBrandingDetails() {
    this._brandingService.uploadFile(this.filesToUpload, this.primaryColor).subscribe(data => {
      this._brandingService.savePrimaryColor(data.brandColor);
      this._toastMessage.success(data.message);
    });
  }
}



