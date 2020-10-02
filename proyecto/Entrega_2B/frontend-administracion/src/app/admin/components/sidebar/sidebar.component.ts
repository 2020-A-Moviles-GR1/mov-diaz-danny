import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { User } from 'src/app/core/models/user.model';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public user: User = {
    username: "Mágody Pendragón",
    password: "",
    full_name: "Danny Díaz"
  };

  public menus_ids: string[] = [
    "menu_function", "menu_gestures", "menu_users"
  ];

  public buttons_menu_pressed  = {}

  constructor(
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    for(var i=0; i<this.menus_ids.length; i++){
      this.buttons_menu_pressed[this.menus_ids[i]] = false;
    }

    if(this.authService.user_admin != null){
      this.user = this.authService.user_admin;
    }
    console.log(this.user)

  }  

  toggleMenuSelection(positionId: number): void {
    this.buttons_menu_pressed[this.menus_ids[positionId]] = !this.buttons_menu_pressed[this.menus_ids[positionId]];
  }
  
  sideBarOpen = true;

  sideBarToggler() {
    this.sideBarOpen = !this.sideBarOpen;
  }

}
