import { User } from 'src/app/core/models/user.model';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { ServerResponse } from '../models/server-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  route: string = "users";

  public user_admin: any;
  objectEdit: User;


  constructor(
    private http: HttpClient
  ) {
    this.objectEdit = null;
  }

  auth(username: string, password: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "auth",
      username: username, password: password
    });
  }

  isAdminLoggedIn(): boolean{
    this.user_admin = JSON.parse(localStorage.getItem("user_admin"));
    return this.user_admin != null && this.user_admin != undefined;
  }

  setCookieLogginAdmin(user: any) {
    localStorage.setItem("user_admin", JSON.stringify(user))
    // console.log(JSON.parse(localStorage.getItem("user")))
  }

  logOutAdmin() {
    localStorage.removeItem("user_admin")
    this.user_admin = null
  }

  create(username: string, password: string, full_name: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "create",
      username: username, password: password, full_name: full_name
    });
  }

  getAllBySkipStep(skip: number, step: number){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "get",
      skip: skip, step: step
    });
  }

  update(username: string, password: string, full_name: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "update",
      username: username, password: password, full_name: full_name
    });
  }

  search(search_method: number, search_value: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "search",
      search_method: search_method, search_value: search_value
    });
  }

  delete(username: string){
    return this.http.post<ServerResponse>(environment.url_api_gate, {
      route: this.route, filename: "delete",
      username: username
    });
  }
  
}
