import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { AdoptionDTO } from '../model/AdoptionDTO';

@Injectable({
  providedIn: 'root',
})
export class AdoptionService {

  private http = inject(HttpClient);
  private api : string = 'http://127.0.0.1:8080/shelter/adoption';
  private auth: AuthService = inject(AuthService);

  public iniciateAdoption(petId: number) {
    
    return this.http.get<AdoptionDTO>(`${this.api}/initiate/${petId}`,{headers: this.auth.getHeaders()});
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
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/accept/${adoptionId}`,{headers: this.auth.getHeaders()});
  }
  
  public declineAdoption(adoptionId: number) {
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/decline/${adoptionId}`,{headers: this.auth.getHeaders()});
  }
  
  // No es para borrar cualquier adopcion, solo se pueden borrar las que estan sin cerrar
  public deleteAdoption(adoptionId: number) {
    
    return this.http.get<AdoptionDTO[]>(`${this.api}/decline/${adoptionId}`,{headers: this.auth.getHeaders()});
  }

}
