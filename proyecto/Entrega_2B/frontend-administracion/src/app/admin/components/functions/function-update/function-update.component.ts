import { ServerResponse } from './../../../../core/models/server-response.model';
import { Router } from '@angular/router';
import { FunctionService } from 'src/app/core/services/function.service';
import { MyFunction } from './../../../../core/models/myfunction.model';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-function-update',
  templateUrl: './function-update.component.html',
  styleUrls: ['./function-update.component.scss']
})
export class FunctionUpdateComponent implements OnInit {

  objectData: MyFunction;
  show_progress: boolean;
  formGroup: FormGroup;

  url_back: string = '/admin/functions/list'

  constructor(
    private dataService: FunctionService,
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
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      function_type_id: ['', [Validators.required, Validators.min(1), Validators.max(3)]],

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

      this.dataService.update(form.id, form.name, form.description, form.function_type_id)
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
