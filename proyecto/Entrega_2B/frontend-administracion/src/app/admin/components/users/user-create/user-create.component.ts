import { Encrypt } from './../../../../utils/encrypt';
import { AuthService } from 'src/app/core/services/auth.service';
import { User } from 'src/app/core/models/user.model';
import { ServerResponse } from './../../../../core/models/server-response.model';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss']
})
export class UserCreateComponent implements OnInit {

  show_progress: boolean;
  formGroup: FormGroup;

  url_back: string = '/admin/users/list';

  users: User[];

  

  constructor(
    private authService: AuthService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    this.show_progress = false;
    
    this.buildForm();
  }

  private buildForm() {
    this.formGroup = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      confirm_password: ['', [Validators.required]],
      full_name: ['', [Validators.required]],
    });
  }

  back() {
    this.router.navigate([this.url_back]);
  }

  create(event: Event) {
    event.preventDefault();  // evitar recargar la p+agina

    if(this.formGroup.valid){
      const form = this.formGroup.value;

      if(form.password != form.confirm_password){
        alert("Contraseñas no coinciden")
        return;
      }

      // se puede enviar solo "changes", con Partial<MyFunction>

      this.show_progress = true;

      this.authService.create(
        form.username, Encrypt.encryptPasswordWithSHA256(form.password), form.full_name
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
