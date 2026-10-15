import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { UserDTO } from '../model/UserDTO';
import { UserUpdateDTO } from '../model/UserUpdateDTO';
import { UserSession } from '../model/UserSession';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private http = inject(HttpClient);
  private api : string = 'http://127.0.0.1:8080/shelter/user';
  private auth: AuthService = inject(AuthService);

    public getUsers() {
  
      return this.http.get<UserDTO[]>(`${this.api}`,{headers: this.auth.getHeaders()});
    }

    public editUser(userUpdate: UserUpdateDTO) {
  
      return this.http.put<UserSession>(`${this.api}/update`,userUpdate,{headers: this.auth.getHeaders()});
    }
    
    public deleteUser(email: string) {

      return this.http.delete<void>(`${this.api}/delete/${encodeURIComponent(email)}`,{headers:this.auth.getHeaders()});
    }
}
