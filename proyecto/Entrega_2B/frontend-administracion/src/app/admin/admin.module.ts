import { MaterialModule } from './../material/material.module';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CoreModule } from './../core/core.module';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { FunctionsListComponent } from './components/functions/functions-list/functions-list.component';
import { GesturesListComponent } from './components/gestures/gestures-list/gestures-list.component';
import { SamplesListComponent } from './components/samples/samples-list/samples-list.component';
import { UsersListComponent } from './components/users/users-list/users-list.component';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { FunctionUpdateComponent } from './components/functions/function-update/function-update.component';
import { GestureUpdateComponent } from './components/gestures/gesture-update/gesture-update.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SampleCreateComponent } from './components/samples/sample-create/sample-create.component';
import { SampleUpdateComponent } from './components/samples/sample-update/sample-update.component';


@NgModule({
  declarations: [
    FunctionsListComponent, 
    GesturesListComponent, 
    SamplesListComponent, 
    UsersListComponent, 
    UserCreateComponent, 
    UserUpdateComponent, 
    FunctionUpdateComponent, 
    GestureUpdateComponent,
    SidebarComponent,
    SampleCreateComponent,
    SampleUpdateComponent,
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    SharedModule,
    ReactiveFormsModule,
    MaterialModule
  ]
})
export class AdminModule { }
