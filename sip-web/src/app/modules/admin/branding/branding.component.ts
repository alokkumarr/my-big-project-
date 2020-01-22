import { Component } from '@angular/core';
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

  constructor(
    private _brandingService: BrandingService,
    public _toastMessage: ToastService
  ) {}

  ngOnInit(): void {
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



