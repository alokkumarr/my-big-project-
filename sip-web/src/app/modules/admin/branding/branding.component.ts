import { Component } from '@angular/core';
import { BrandingService } from './branding.service';
import { ToastService } from '../../../common/services';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as isEmpty from 'lodash/isEmpty';
@Component({
  selector: 'admin-branding',
  templateUrl: './branding.component.html',
  styleUrls: ['./branding.component.scss']
})
export class AdminBrandingComponent {
  primaryColor;
  filesToUpload = [];
  binaryFormatImage: any;
  brandImage: any;
  public saveForm: FormGroup;

  constructor(
    private _brandingService: BrandingService,
    public _toastMessage: ToastService,
    public fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.saveForm = this.fb.group({
      color: [this.primaryColor, [Validators.required,
        Validators.maxLength(30)]],
      logo: ['', []]
    });

    this._brandingService.getBrandingDetails().subscribe(data => {
      this.primaryColor = isEmpty(data.brandColor) ? '0077be' : data.brandColor;
      this.brandImage = 'data:image/gif;base64,' + data.brandImage;
      this._brandingService.savePrimaryColor(this.primaryColor);
    });
  }

  prepareBrandimgForUpload(event) {
    this.filesToUpload = event.srcElement.files;
    this.binaryFormatImage = this.filesToUpload[0].name;
  }

  saveBrandingDetails() {
    this._brandingService.uploadFile(this.filesToUpload, this.saveForm.get('color').value).subscribe(data => {
      this._toastMessage.success(data.message);
      window.location.reload();
    });
  }

  resetBrandingData() {
    this._brandingService.reset().subscribe(data => {
      window.location.reload();
    });
  }
}



