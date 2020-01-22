import { Component } from '@angular/core';
import { BrandingService } from './branding.service';
import { ToastService } from '../../../common/services';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'admin-branding',
  templateUrl: './branding.component.html',
  styleUrls: ['./branding.component.scss']
})
export class AdminBrandingComponent {
  primaryColor = '#bb0000';
  filesToUpload = [];
  binaryFormatImage: any;
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
  }

  prepareBrandimgForUpload(event) {
    this.filesToUpload = event.srcElement.files;
    this.binaryFormatImage = this.filesToUpload[0].name;
  }

  saveBrandingDetails() {
    this._brandingService.uploadFile(this.filesToUpload, this.saveForm.get('color').value).subscribe(data => {
      this._brandingService.savePrimaryColor(data.brandColor);
      this._toastMessage.success(data.message);
    });
  }
}



