import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Gesture } from '../models/gesture.model';
import { Sample } from '../models/sample.model';
import { ServerResponse } from '../models/server-response.model';

@Injectable({
  providedIn: 'root'
})
export class SampleService {

  route: string = "samples";

  objectEdit: Sample;

  gestureParent: Gesture;

  constructor(
    private http: HttpClient
  ) {
    this.objectEdit = null;
    this.gestureParent = null;
  }

  setGestureParent(gesture: Gesture){
    this.gestureParent = gesture;
  }

  create(
    id_function: number, username_user: string, is_augmented_data: boolean,
    data_accelerometer_x: string, data_accelerometer_y: string, data_accelerometer_z: string,
    data_gyroscope_x: string, data_gyroscope_y: string, data_gyroscope_z: string
    ){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "create",
      id_function: id_function, username_user: username_user, is_augmented_data: is_augmented_data,
      data_accelerometer_x: data_accelerometer_x, data_accelerometer_y: data_accelerometer_y, data_accelerometer_z: data_accelerometer_z,
      data_gyroscope_x: data_gyroscope_x, data_gyroscope_y: data_gyroscope_y, data_gyroscope_z: data_gyroscope_z
    });
  }

  getAllForGesture(id_function: number, username_user: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "get_for_gesture",
      id_function: id_function, username_user: username_user
    });
  }

  update(
    id: number, is_augmented_data: boolean,
    data_accelerometer_x: string, data_accelerometer_y: string, data_accelerometer_z: string,
    data_gyroscope_x: string, data_gyroscope_y: string, data_gyroscope_z: string
  ){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "update",
      id: id, is_augmented_data: is_augmented_data,
      data_accelerometer_x: data_accelerometer_x, data_accelerometer_y: data_accelerometer_y, data_accelerometer_z: data_accelerometer_z,
      data_gyroscope_x: data_gyroscope_x, data_gyroscope_y: data_gyroscope_y, data_gyroscope_z: data_gyroscope_z
    });
  }


  search(search_method: number, search_value: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "search",
      search_method: search_method, search_value: search_value
    });
  }

  delete(id: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "delete",
      id: id
    });
  }
  
}
