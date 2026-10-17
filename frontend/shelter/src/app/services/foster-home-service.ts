import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { Observable } from 'rxjs';
import { FosterHomeDTO } from '../model/FosterHomeDTO';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})

// Este servicio devuelve una peticion para cada uno de lso endpoints que tiene el backend derivados de /fosterhome
export class FosterHomeService {

  private api: string = environment.api+"/fosterhome";
  private http: HttpClient = inject(HttpClient)
  private auth: AuthService= inject(AuthService);

  public createFosterHome(fosterHome: FosterHomeDTO){

    return this.http.post<FosterHomeDTO>(`${this.api}/create`,fosterHome,{headers: this.auth.getHeaders()});
  }

  public getFosterHomes() {

    return this.http.get<FosterHomeDTO[]>(`${this.api}`,{headers: this.auth.getHeaders()});
  }

  public editFosterHome(fosterHome: FosterHomeDTO) {

    return this.http.put<FosterHomeDTO>(`${this.api}/edit`,fosterHome,{headers:this.auth.getHeaders()});
  }

  public deleteFosterHome(id: number) {

    return this.http.delete<void>(`${this.api}/delete/${id}`,{headers:this.auth.getHeaders()});
  }
}
