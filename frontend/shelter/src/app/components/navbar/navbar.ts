import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Session } from '../session/session';
import { AuthService } from '../../services/auth-service';

@Component({
  selector: 'app-navbar',
  // en imports tiene que ir solamente lo que se use en la plantilla
  imports: [RouterLink, Session],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  public authService = inject(AuthService);
}
