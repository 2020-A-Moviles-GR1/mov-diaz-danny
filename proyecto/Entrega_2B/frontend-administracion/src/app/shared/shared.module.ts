import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../material/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FormatStringListPipe } from './pipes/format-string-list.pipe';



@NgModule({
  declarations: [HeaderComponent, FooterComponent, FormatStringListPipe],
  imports: [
    CommonModule,
    RouterModule,
    MaterialModule
    
  ],
  exports: [HeaderComponent, FooterComponent, FormatStringListPipe]
})
export class SharedModule { }
