import { SampleCreateComponent } from './components/samples/sample-create/sample-create.component';
import { SampleUpdateComponent } from './components/samples/sample-update/sample-update.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { UserUpdateComponent } from './components/users/user-update/user-update.component';
import { UsersListComponent } from './components/users/users-list/users-list.component';
import { SamplesListComponent } from './components/samples/samples-list/samples-list.component';
import { GestureUpdateComponent } from './components/gestures/gesture-update/gesture-update.component';
import { GesturesListComponent } from './components/gestures/gestures-list/gestures-list.component';
import { FunctionUpdateComponent } from './components/functions/function-update/function-update.component';
import { FunctionsListComponent } from './components/functions/functions-list/functions-list.component';

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from '../components/page-not-found/page-not-found.component';


const routes: Routes = [
  {
    path: '',
    component: SidebarComponent,
    children: [
      {  
        path: '',
        redirectTo: '/admin/functions/list',
        pathMatch: 'full',  // la url sin ningun path
      },
      { 
        path: 'functions/list', 
        component: FunctionsListComponent,  
      },
      { 
        path: 'functions/update', 
        component: FunctionUpdateComponent,  
      },
      { 
        path: 'gestures/list', 
        component: GesturesListComponent,  
      },
      {  
        path: 'gestures/update', 
        component: GestureUpdateComponent,  
      },
      { 
        path: 'samples/list',  
        component: SamplesListComponent, 
      },
      { 
        path: 'samples/update',  
        component: SampleUpdateComponent, 
      },
      { 
        path: 'samples/create',  
        component: SampleCreateComponent, 
      },
      {  
        path: 'users/list', 
        component: UsersListComponent,  
      },
      {  
        path: 'users/create',  
        component: UserCreateComponent, 
      },
      {  
        path: 'users/update',  
        component: UserUpdateComponent,  
      },
      {  // este se deja para el final
        path: '**',  // significa que no hubo match
        component: PageNotFoundComponent,  // se puede redirigir a 404 o al home!
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
