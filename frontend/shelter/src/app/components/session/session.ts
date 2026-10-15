import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-session',
  imports: [],
  templateUrl: './session.html',
  styleUrl: './session.css',
})

// Esta clase simplemente se encarga de actualizar el boton de inicar/cerrar sesion y el nombre que aparece junto al Bienvenido
export class Session {

  private authService = inject(AuthService);
  public username = computed( () => this.authService.username() ?? "invitado"); 

}
