import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth-service';
import { UserDTO } from '../model/UserDTO';
import { UserUpdateDTO } from '../model/UserUpdateDTO';
import { UserSession } from '../model/UserSession';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})
export class UserService {

  private api : string = environment.api+'/user';
  private http = inject(HttpClient);
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
