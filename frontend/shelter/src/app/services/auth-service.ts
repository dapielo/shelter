import { computed, inject, Injectable, signal } from '@angular/core';
import { UserSession } from '../model/UserSession';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { UserDTO } from '../model/UserDTO';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root',
})

// Servicio que se encarga de gestionar los tokens
export class AuthService {

  private router = inject(Router)
  // Injectamos el servicio httpClient
  private http = inject(HttpClient);
  // Definimos el endpoint
  private api: string = environment.api;

  // lo definimos como signal para que avise a cualquier componente que usa este valor de si cambia
  public userSession = signal<UserSession | null> (this.loadUser());

  // Computed poruqe su valor depende de un signal, y tiene que suscribirse a este para que su valor se recalcule si el signal cambia, sino no se actualiza
  public isLogged = computed(() => this.userSession() != null);
  // El primer ? devuelve undefined si el objeto es null, y el segundo ?? convierte ese undefined en lo que le digamos
  public token = computed(() => this.userSession()?.token ?? null);
  public username = computed(() => this.userSession()?.username ?? null);
  public userId = computed(() => this.userSession()?.id ?? null);

  // Devuelve las cabeceras de autenticacion para insertar en las peticiones que la necesitan
  getHeaders() : HttpHeaders {
    const token = this.token();
    if (!token) {
      return new HttpHeaders()
    }
    const headers = new HttpHeaders();
    return headers.append("Authorization",`Bearer ${token}`);
  }

  // Carga el UserSession desde el localStorage
  loadUser() : UserSession | null {
    // el getItem me devuelve un JSON o null si no lo encuentra
    const saved = localStorage.getItem("userSession");
    if (!saved) {
      return null;
    }
    try {
      return JSON.parse(saved);
    } catch (error) {
      return null;
    }
  }

  // Hace la peticion de token de autenticacion a la API
  login(email: string, password: string) : Observable<UserSession>{

    // Creamos el JSON a partir de los parametros
    let login = {
      "email":email,
      "password":password
    }

    // cosntruimos la peticion, recordar que esto devuelve un Observable, tenemos uqe subscribirnos a el para que la peticion se ejecute de 
    // forma efectiva y reciba los datos
    return this.http.post<UserSession>(`${this.api}/login`, login);
  }

  // Guardar datos de la sesion cuando se haga el login (en el localStorage)
  saveSession(session: UserSession) : void {

    // guardamos la sesion, no hacemos try porque este objeto session lo creamos nosotros, si esta mal da error de sintaxis
    localStorage.setItem("userSession", JSON.stringify(session));
    // Actualizamos el atributo userSession
    this.userSession.set(session);
  }

  // para cerrar sesion solo borramos el objeto userSession del localStorage
  logout() {
    localStorage.removeItem("userSession");
    this.userSession.set(null);
    // El propio servicio nos lleva al inicio si cerramos sesion
    this.router.navigate(["/"]);
  }

  // metodo para registarse 
  register(user: UserDTO) {
    // Devuelve un Observable el que se pueden subscribir en la llamada
    return this.http.post<UserSession>(`${this.api}/register`, user);
  }
}
