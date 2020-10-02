import { sha256 } from 'js-sha256';

export class Encrypt {


  public static encryptPasswordWithSHA256(rawPassword: string) : string{
    return sha256(rawPassword);
  }


}
