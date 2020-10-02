import { Gesture } from './../../../../core/models/gesture.model';
import { SampleService } from './../../../../core/services/sample.service';
import { Sample } from './../../../../core/models/sample.model';

import { ServerResponse } from "./../../../../core/models/server-response.model";
import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";
import { MatTableDataSource } from '@angular/material/table';


@Component({
  selector: 'app-samples-list',
  templateUrl: './samples-list.component.html',
  styleUrls: ['./samples-list.component.scss']
})
export class SamplesListComponent implements OnInit {

  listData: Sample[] = [];
  root_dir: string = "/admin/samples";
  dataSource: MatTableDataSource<Sample>;

  gestureParent: Gesture;

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
      description: "Buscar por fecha hora (yyyy-mm-dd)",
    },
  ];

  data_exists: boolean;

  displayedColumns: string[] = [
    "actions",
    "id",
    "datetime",
    "is_augmented_data",
    "data_accelerometer_x",
    "data_accelerometer_y",
    "data_accelerometer_z",
    "data_gyroscope_x",
    "data_gyroscope_y",
    "data_gyroscope_z",
  ];

  url_back: string = "/admin/gestures/list";

  constructor(
    private formBuilder: FormBuilder,
    private dataService: SampleService,
    private router: Router
  ) {
    this.is_search = false;
    this.data_exists = true;
    this.show_progress = false;
    this.gestureParent = dataService.gestureParent;
    if(this.gestureParent == null || this.gestureParent == undefined){
      this.back();
    }
    else{
      this.buildForm();
    }
  }

  ngOnInit(): void {
    this.fetchData();
  }

  getAll() {
    this.is_search = false;
    this.listData = [];
    this.fetchData();
  }

  fetchData() {
    
    this.show_progress = true;
    this.dataService
      .getAllForGesture(this.gestureParent.id_function, this.gestureParent.username_user)

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
          }
  
          this.listData = this.listData.concat(response.data);
          this.dataSource = new MatTableDataSource<Sample>(this.listData);
        }

       
      });
  }

  back() {
    this.router.navigate([this.url_back]);
  }

  delete(object_to_delete: Sample) {
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
            this.dataSource = new MatTableDataSource<Sample>(this.listData);
          }else{
            alert(response.server_message);
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
              this.dataSource = new MatTableDataSource<Sample>(this.listData);
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
