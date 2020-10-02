import { SampleService } from './services/sample.service';
import { GestureService } from './services/gesture.service';
import { FunctionService } from './services/function.service';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';


@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  providers: [
    AuthService,
    FunctionService,
    GestureService,
    SampleService
  ]
})
export class CoreModule { }
