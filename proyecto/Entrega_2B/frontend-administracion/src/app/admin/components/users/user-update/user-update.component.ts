import { Encrypt } from './../../../../utils/encrypt';
import { AuthService } from 'src/app/core/services/auth.service';
import { User } from 'src/app/core/models/user.model';
import { ServerResponse } from './../../../../core/models/server-response.model';
import { Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';

@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.scss']
})
export class UserUpdateComponent implements OnInit {

  objectData: User;
  show_progress: boolean;
  formGroup: FormGroup;

  url_back: string = '/admin/users/list';

  saved_last_pass: string = "";

  constructor(
    private dataService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.show_progress = false;
    this.objectData = this.dataService.objectEdit;

    if(this.objectData == undefined || this.objectData == null){
      this.back();
    }else{
      this.buildForm();
      this.saved_last_pass = this.objectData.password;
      this.objectData.password = "";
      this.formGroup.patchValue(this.objectData);
    }
  }
  
  ngOnInit(): void {
  }

  private buildForm() {
    this.formGroup = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', []],
      confirm_password: ['', []],
      full_name: ['', [Validators.required]],
    });
  }

  back() {
    this.router.navigate([this.url_back]);
  }

  update(event: Event) {
    event.preventDefault();  // evitar recargar la p+agina

    if(this.formGroup.valid){
      const form = this.formGroup.value;

      if(form.password != ""){
        if(form.password != form.confirm_password){
          alert("Contraseñas no coinciden")
          return;
        }
        form.password = Encrypt.encryptPasswordWithSHA256(form.password);
      }else{
        form.password = this.saved_last_pass;
      }
      

      // se puede enviar solo "changes", con Partial<MyFunction>

      this.show_progress = true;

      this.dataService.update(form.username, form.password, form.full_name)
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
