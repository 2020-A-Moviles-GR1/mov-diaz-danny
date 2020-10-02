import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Gesture } from '../models/gesture.model';
import { ServerResponse } from '../models/server-response.model';
@Injectable({
  providedIn: 'root'
})
export class GestureService {

  route: string = "gestures";

  objectEdit: Gesture;

  constructor(
    private http: HttpClient
  ) {
    this.objectEdit = null;
  }

  getAllBySkipStep(skip: number, step: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "get",
      skip: skip, step: step
    });
  }

  update(id_function: number, username_user: string, name: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "update",
      id_function: id_function, username_user: username_user, name: name
    });
  }

  search(search_method: number, search_value: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "search",
      search_method: search_method, search_value: search_value
    });
  }

  delete(id_function: number, username_user: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "delete",
      id_function: id_function, username_user: username_user
    });
  }
  
}
