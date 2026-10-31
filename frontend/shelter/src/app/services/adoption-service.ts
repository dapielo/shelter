import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { AdoptionDTO } from '../model/AdoptionDTO';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AdoptionService {

  private api : string = environment.api+'/adoption';
  private http = inject(HttpClient);
  private auth: AuthService = inject(AuthService);

  public iniciateAdoption(petId: number) {
    
    return this.http.post<AdoptionDTO>(`${this.api}/initiate/${petId}`,null,{headers: this.auth.getHeaders()});
  }

  public getAdoptionsAsAdopter() {
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/asAdopter`,{headers: this.auth.getHeaders()});
  }
  
  public getAdoptionsAsOwner() {
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/asOwner`,{headers: this.auth.getHeaders()});
  }
  
  public getHistory() {
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/closed`,{headers: this.auth.getHeaders()});
  }
  
  public acceptAdoption(adoptionId: number) {
    
    return this.http.post<void>(`${this.api}/accept/${adoptionId}`,null,{headers: this.auth.getHeaders()});
  }
  
  public declineAdoption(adoptionId: number) {
    
    return this.http.put<void>(`${this.api}/decline/${adoptionId}`,null,{headers: this.auth.getHeaders()});
  }
  
  // No es para borrar cualquier adopcion, solo se pueden borrar las que estan sin cerrar, pide el id de la mascota
  public deleteAdoption(petId: number) {
    
    return this.http.delete<void>(`${this.api}/delete/${petId}`,{headers: this.auth.getHeaders()});
  }

}
