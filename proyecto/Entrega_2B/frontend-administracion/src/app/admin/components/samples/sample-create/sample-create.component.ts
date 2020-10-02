import { AuthService } from 'src/app/core/services/auth.service';
import { FunctionService } from 'src/app/core/services/function.service';
import { User } from 'src/app/core/models/user.model';
import { MyFunction } from './../../../../core/models/myfunction.model';
import { ServerResponse } from './../../../../core/models/server-response.model';
import { SampleService } from './../../../../core/services/sample.service';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import {map, startWith} from 'rxjs/operators';

@Component({
  selector: 'app-sample-create',
  templateUrl: './sample-create.component.html',
  styleUrls: ['./sample-create.component.scss']
})
export class SampleCreateComponent implements OnInit {

  show_progress: boolean;
  formGroup: FormGroup;

  username_userField: FormControl;

  url_back: string = '/admin/samples/list';

  functions: MyFunction[] = [];
  users: User[];

  

  constructor(
    private functionService: FunctionService,
    private authService: AuthService,
    private dataService: SampleService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.show_progress = false;
    
    this.username_userField = new FormControl('', [Validators.required]);

    
    
    this.username_userField.valueChanges
      .subscribe(value => {
          // cada vez que se produce un cambio
          console.log(value);
          if(value.length > 0){
            this.authService.search(0, value)
            .subscribe((response: ServerResponse) => {
              console.log("Search", response)
              this.users = response.data;
            })
          }
          
      });
      
    this.fetchDataForSelectForm();
    this.buildForm();
  }


  private fetchDataForSelectForm() {
    this.functionService.getFunctionsForForm()
      .subscribe((response: ServerResponse) => {
        this.functions = response.data;
      })
  }

  private buildForm() {
    this.formGroup = this.formBuilder.group({
      id_function: ['', [Validators.required]],
      is_augmented_data: ['', [Validators.required]],
      data_accelerometer_x: ['', [Validators.required]],
      data_accelerometer_y: ['', [Validators.required]],
      data_accelerometer_z: ['', [Validators.required]],
      data_gyroscope_x: ['', [Validators.required]],
      data_gyroscope_y: ['', [Validators.required]],
      data_gyroscope_z: ['', [Validators.required]],
    });
    this.formGroup.addControl("username_user", this.username_userField)
  }

  back() {
    this.router.navigate([this.url_back]);
  }

  create(event: Event) {
    event.preventDefault();  // evitar recargar la p+agina

    if(this.formGroup.valid){
      const form = this.formGroup.value;

      // se puede enviar solo "changes", con Partial<MyFunction>

      this.show_progress = true;

      this.dataService.create(
        form.id_function, form.username_user, form.is_augmented_data, form.data_accelerometer_x,
        form.data_accelerometer_y, form.data_accelerometer_z, form.data_gyroscope_x, 
        form.data_gyroscope_y, form.data_gyroscope_z
        ).subscribe((response: ServerResponse) => {
          console.log("create", response);
          this.show_progress = false;

          if(response.id_message == 1){
            this.back();
          }else{
            alert(response.server_message);
          }
        });
    }



  }

  ngOnInit(): void {
  }


}
