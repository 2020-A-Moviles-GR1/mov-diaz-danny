import { SampleService } from './../../../../core/services/sample.service';
import { GestureService } from './../../../../core/services/gesture.service';
import { Gesture } from './../../../../core/models/gesture.model';
import { ServerResponse } from "./../../../../core/models/server-response.model";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-gestures-list',
  templateUrl: './gestures-list.component.html',
  styleUrls: ['./gestures-list.component.scss']
})
export class GesturesListComponent implements OnInit {

  listData: Gesture[] = [];
  root_dir: string = "/admin/gestures";
  dataSource: MatTableDataSource<Gesture>;

  skip: number;
  step: number;
  is_search: boolean;

  formGroup: FormGroup;

  show_progress: boolean;

  search_methods = [
    {
      code: 0,
      description: "Buscar por id de función",
    },
    {
      code: 1,
      description: "Buscar por nombre de usuario",
    },
    {
      code: 2,
      description: "Buscar por nombre de gesto",
    },
  ];

  data_exists: boolean;

  displayedColumns: string[] = [
    "actions",
    "id_function",
    "username_user",
    "name"
  ];

  constructor(
    private formBuilder: FormBuilder,
    private dataService: GestureService,
    private dataSamplesService: SampleService,
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
          this.dataSource = new MatTableDataSource<Gesture>(this.listData);
        }

       
      });
  }

  getMore() {
    this.skip += this.step;
    this.fetchData(this.skip, this.step);
  }

  watchSamples(parent_gesture: Gesture) {
    console.log("Objeto padre a observar", parent_gesture);
    this.dataSamplesService.setGestureParent(parent_gesture);
    this.router.navigate(["/admin/samples/list"]);
  }

  delete(object_to_delete: Gesture) {
    console.log("Objeto a eliminar", object_to_delete);

    this.show_progress = true;

     this.dataService.delete(object_to_delete.id_function, object_to_delete.username_user)
       .subscribe((response: ServerResponse) => {
        this.show_progress = false;
        console.log("Respuesta de eliminar", response)

        if(response.id_message == undefined){
          alert("Ha ocurrido un error de conexión")
        }else{
          if(response.id_message == 1){
            for (let i = 0; i < this.listData.length; i++) {
              if(
                this.listData[i].id_function == object_to_delete.id_function && 
                this.listData[i].username_user == object_to_delete.username_user
                ){
                this.listData.splice(i, 1);
                break;
              }
 
            }
            this.dataSource = new MatTableDataSource<Gesture>(this.listData);
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
              this.dataSource = new MatTableDataSource<Gesture>(this.listData);
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
