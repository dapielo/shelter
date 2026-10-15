import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { PetDTO } from '../model/PetDTO';

@Injectable({
  providedIn: 'root',
})

// tiene un metodo para cada endpoint del backend que deriva de /pet
export class PetService {

  private api: string = "http://127.0.0.1:8080/shelter/pet";
  private http: HttpClient = inject(HttpClient)
  private auth: AuthService= inject(AuthService);

  // Necesita un formData por si enviamos la imagen, es la unica peticion que requiere un formData junto a editPet
  public createPet(pet: FormData) {

    return this.http.post<PetDTO>(`${this.api}/create`,pet,{headers: this.auth.getHeaders()});
  }

  public getPet(id : number) {
    
    return this.http.get<PetDTO>(`${this.api}/id/${id}`);
  }

  public getPetsByProvince(province : string) {
    
    return this.http.get<PetDTO[]>(`${this.api}/province/${province}`);
  }

  public getVeteranPets() {
    
    return this.http.get<PetDTO[]>(`${this.api}/veterans`);
  }

  public editPet(pet: FormData) {

    return this.http.put<PetDTO>(`${this.api}/update`,pet,{headers:this.auth.getHeaders()});
  }

  public deleteImage(id: number) {

    return this.http.put<void>(`${this.api}/image/delete/${id}`,null,{headers:this.auth.getHeaders()});
  }

  public deletePet(id: number) {

    return this.http.delete<void>(`${this.api}/delete/${id}`,{headers:this.auth.getHeaders()});
  }
}
