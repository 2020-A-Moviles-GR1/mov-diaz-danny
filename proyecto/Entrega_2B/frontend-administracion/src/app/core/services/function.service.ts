import { MyFunction } from './../models/myfunction.model';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ServerResponse } from '../models/server-response.model';

@Injectable({
  providedIn: 'root'
})
export class FunctionService {

  route: string = "functions";

  objectEdit: MyFunction;

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

  update(id: number, name: string, description: string, function_type_id: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "update",
      id: id, name: name, description: description, function_type_id: function_type_id
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

  getFunctionsForForm(){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "get_all"
    });
  }
}
