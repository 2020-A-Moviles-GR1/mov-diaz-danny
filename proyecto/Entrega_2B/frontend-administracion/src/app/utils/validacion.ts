
export class Validacion {


  public static isValidMail(correo: string) : boolean{

    if(correo != ""){
      if(correo.includes("@") && correo.includes(".")){
        return true;
      }
    }

    return false;
  }


}
