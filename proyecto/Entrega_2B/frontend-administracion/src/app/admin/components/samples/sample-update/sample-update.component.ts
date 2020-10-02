import { Sample } from './../../../../core/models/sample.model';
import { SampleService } from './../../../../core/services/sample.service';
import { ServerResponse } from './../../../../core/models/server-response.model';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-sample-update',
  templateUrl: './sample-update.component.html',
  styleUrls: ['./sample-update.component.scss']
})
export class SampleUpdateComponent implements OnInit {

  objectData: Sample;
  show_progress: boolean;
  formGroup: FormGroup;

  url_back: string = '/admin/samples/list'

  constructor(
    private dataService: SampleService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.show_progress = false;
    this.objectData = this.dataService.objectEdit;

    if(this.objectData == undefined || this.objectData == null){
      this.back();
    }else{
      this.buildForm();
      this.formGroup.patchValue(this.objectData);
    }
  }
  
  ngOnInit(): void {
  }

  private buildForm() {
    this.formGroup = this.formBuilder.group({
      id: ['', [Validators.required]],
      id_function: ['', [Validators.required]],
      username_user: ['', [Validators.required]],
      datetime: ['', [Validators.required]],
      is_augmented_data: ['', [Validators.required]],
      data_accelerometer_x: ['', [Validators.required]],
      data_accelerometer_y: ['', [Validators.required]],
      data_accelerometer_z: ['', [Validators.required]],
      data_gyroscope_x: ['', [Validators.required]],
      data_gyroscope_y: ['', [Validators.required]],
      data_gyroscope_z: ['', [Validators.required]],

    });
  }

  back() {
    this.router.navigate([this.url_back]);
  }

  update(event: Event) {
    event.preventDefault();  // evitar recargar la p+agina

    if(this.formGroup.valid){
      const form = this.formGroup.value;

      // se puede enviar solo "changes", con Partial<MyFunction>

      this.show_progress = true;

      this.dataService.update(
        form.id, form.is_augmented_data, form.data_accelerometer_x,
        form.data_accelerometer_y, form.data_accelerometer_z, form.data_gyroscope_x, 
        form.data_gyroscope_y, form.data_gyroscope_z
        ).subscribe((response: ServerResponse) => {
          console.log("update", response);
          this.show_progress = false;

          if(response.id_message == 1){
            this.back();
          }else{
            alert(response.server_message);
          }
        });
    }



  }

}
