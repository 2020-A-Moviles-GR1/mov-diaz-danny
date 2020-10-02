import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'formatStringList'
})
export class FormatStringListPipe implements PipeTransform {

  transform(list_strings: string[]): string {
    var output: string = "";

    if(list_strings.length > 0){
      list_strings.forEach((value, index) => {
        output += value
        if(index < list_strings.length-2){
          output += ", "
        }else if(index == list_strings.length-2){
          output += " y "
        }else if(index == list_strings.length-1){
          output += "."
        }

      })
    }else{
      output = "Ninguno";
    }

    return output;
  }

}
