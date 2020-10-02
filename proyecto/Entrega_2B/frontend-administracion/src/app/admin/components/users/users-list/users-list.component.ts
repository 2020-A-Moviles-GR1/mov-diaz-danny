import { User } from 'src/app/core/models/user.model';
import { AuthService } from 'src/app/core/services/auth.service';
import { ServerResponse } from "./../../../../core/models/server-response.model";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-users-list',
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit {

  listData: User[] = [];
  root_dir: string = "/admin/users";
  dataSource: MatTableDataSource<User>;

  skip: number;
  step: number;
  is_search: boolean;

  formGroup: FormGroup;

  show_progress: boolean;

  search_methods = [
    {
      code: 0,
      description: "Buscar por username",
    },
    {
      code: 1,
      description: "Buscar por nombre completo",
    },
  ];

  data_exists: boolean;

  displayedColumns: string[] = [
    "actions",
    "username",
    "password",
    "full_name",
  ];

  constructor(
    private formBuilder: FormBuilder,
    private dataService: AuthService,
    private router: Router
  ) {
    this.is_search = false;
    this.data_exists = true;
    this.show_progress = false;
    this.buildForm();
  }

  ngOnInit(): void {
    this.skip = 0;
    this.step = environment.minimum_step_read_table;
    this.fetchData(this.skip, this.step);
  }

  getAll() {
    this.skip = 0;
    this.step = environment.minimum_step_read_table;
    this.is_search = false;
    this.listData = [];
    this.fetchData(this.skip, this.step);
  }

  fetchData(skip: number, step: number) {
    
    this.show_progress = true;
    this.dataService
      .getAllBySkipStep(skip, step)

      .subscribe((response: ServerResponse) => {
        this.show_progress = false;
        console.log("fetchData: ", response);


        if(response.id_message == undefined){
          alert("Ha ocurrido un error de conexión")
        }else{
          if (response.data.length > 0) {
            this.data_exists = true;
          } else {
            this.data_exists = false;
            this.skip -= this.step;
          }
  
          this.listData = this.listData.concat(response.data);
          this.dataSource = new MatTableDataSource<User>(this.listData);
        }

       
      });
  }

  getMore() {
    this.skip += this.step;
    this.fetchData(this.skip, this.step);
  }

  delete(object_to_delete: User) {
    console.log("Objeto a eliminar", object_to_delete);

    this.show_progress = true;

     this.dataService.delete(object_to_delete.username)
       .subscribe((response: ServerResponse) => {
        this.show_progress = false;
        console.log("Respuesta de eliminar", response)

        if(response.id_message == undefined){
          alert("Ha ocurrido un error de conexión")
        }else{
          if(response.id_message == 1){
            for (let i = 0; i < this.listData.length; i++) {
              if(this.listData[i].username == object_to_delete.username){
                this.listData.splice(i, 1);
                break;
              }
 
            }
            this.dataSource = new MatTableDataSource<User>(this.listData);
          }else{
            alert("No es posible eliminar a este usuario");
          }
        }

         

         
       });
  }
  
  navigateToCreate() {
    this.router.navigate([this.root_dir + "/create"]);
  }

  navigateToUpdate(object_to_edit: any) {
    this.dataService.objectEdit = object_to_edit;
    this.router.navigate([this.root_dir + "/update"]);
  }

  searchData(event: Event) {
    event.preventDefault(); // evitar recargar la p+agina

    if (this.formGroup.valid) {
      const form = this.formGroup.value;

      console.log("Se va a enviar ", form);
      this.show_progress = true;

      this.dataService
        .search(form.search_method, form.search_value)
          .subscribe((response: ServerResponse) => {
            this.show_progress = false;
            console.log("searchData: ", response);

            if(response.id_message == undefined){
              alert("Ha ocurrido un error de conexión")
            }else{
			  if (response.data.length > 0) {
                this.data_exists = true;
              } else {
                this.data_exists = false;
              }
              this.listData = response.data;
              this.is_search = true;
              this.dataSource = new MatTableDataSource<User>(this.listData);
            }
            

          });
    }
  }

  private buildForm() {
    this.formGroup = this.formBuilder.group({
      search_method: ["", Validators.required],
      search_value: ["", Validators.required],
    });
  }

}
