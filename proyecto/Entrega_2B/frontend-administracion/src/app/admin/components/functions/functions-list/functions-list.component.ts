
import { ServerResponse } from "./../../../../core/models/server-response.model";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { MyFunction } from "src/app/core/models/myfunction.model";
import { FunctionService } from "src/app/core/services/function.service";
import { environment } from "src/environments/environment";
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: "app-functions-list",
  templateUrl: "./functions-list.component.html",
  styleUrls: ["./functions-list.component.scss"],
})
export class FunctionsListComponent implements OnInit {
  listData: MyFunction[] = [];
  root_dir: string = "/admin/functions";
  dataSource: MatTableDataSource<MyFunction>;

  skip: number;
  step: number;
  is_search: boolean;

  formGroup: FormGroup;

  show_progress: boolean;

  search_methods = [
    {
      code: 0,
      description: "Buscar por id",
    },
    {
      code: 1,
      description: "Buscar por nombre",
    },
    {
      code: 2,
      description: "Buscar por tipo (1, 2, 3)",
    },
  ];

  data_exists: boolean;

  displayedColumns: string[] = [
    "actions",
    "id",
    "name",
    "description",
    "function_type_id",
  ];

  constructor(
    private formBuilder: FormBuilder,
    private dataService: FunctionService,
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
          this.dataSource = new MatTableDataSource<MyFunction>(this.listData);
        }

       
      });
  }

  getMore() {
    this.skip += this.step;
    this.fetchData(this.skip, this.step);
  }

  delete(object_to_delete: MyFunction) {
    console.log("Objeto a eliminar", object_to_delete);

    this.show_progress = true;

     this.dataService.delete(object_to_delete.id)
       .subscribe((response: ServerResponse) => {
        this.show_progress = false;
        console.log("Respuesta de eliminar", response)

        if(response.id_message == undefined){
          alert("Ha ocurrido un error de conexión")
        }else{
          if(response.id_message == 1){
            for (let i = 0; i < this.listData.length; i++) {
              if(this.listData[i].id == object_to_delete.id){
                this.listData.splice(i, 1);
                break;
              }
 
            }
            this.dataSource = new MatTableDataSource<MyFunction>(this.listData);
          }else{
            alert(response.server_message);
          }
        }

         

         
       });
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
              this.dataSource = new MatTableDataSource<MyFunction>(this.listData);
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
