import { Gesture } from './../../../../core/models/gesture.model';
import { GestureService } from './../../../../core/services/gesture.service';
import { ServerResponse } from './../../../../core/models/server-response.model';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-gesture-update',
  templateUrl: './gesture-update.component.html',
  styleUrls: ['./gesture-update.component.scss']
})
export class GestureUpdateComponent implements OnInit {

  objectData: Gesture;
  show_progress: boolean;
  formGroup: FormGroup;

  url_back: string = '/admin/gestures/list'

  constructor(
    private dataService: GestureService,
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
      id_function: ['', [Validators.required]],
      username_user: ['', [Validators.required]],
      name: ['', [Validators.required]],
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

      this.dataService.update(form.id_function, form.username_user, form.name)
        .subscribe((response: ServerResponse) => {
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
