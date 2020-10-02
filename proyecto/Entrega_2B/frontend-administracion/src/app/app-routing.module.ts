import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { AdminGuard } from './admin.guard';
import { BaseLayoutComponent } from './base-layout/base-layout.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/admin/functions/list',
    pathMatch: 'full',  // la url sin ningun path
  },
  {
    path: 'admin',  // significa que no hubo match
    canActivate: [AdminGuard],
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)
  },
  {  // este se deja para el final
    path: '**',  // significa que no hubo match
    component: PageNotFoundComponent,  // se puede redirigir a 404 o al home!
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      preloadingStrategy: PreloadAllModules
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
