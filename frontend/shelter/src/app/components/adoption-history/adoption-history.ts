import { Component, inject, OnInit, signal } from '@angular/core';
import { AdoptionDTO } from '../../model/AdoptionDTO';
import { AdoptionService } from '../../services/adoption-service';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';
import { AdoptionGrid } from '../adoption-grid/adoption-grid';

@Component({
  selector: 'app-adoption-history',
  imports: [AdoptionGrid],
  templateUrl: './adoption-history.html',
  styleUrl: './adoption-history.css',
})
export class AdoptionHistory implements OnInit{

  adoptionService: AdoptionService = inject(AdoptionService);
  authService: AuthService = inject(AuthService);
  router: Router = inject(Router);

  closedAdoptions = signal<AdoptionDTO[]>([]);
  errorMessage = signal<string>('');

  ngOnInit(): void {

    this.adoptionService.getHistory().subscribe({
      next: (data: AdoptionDTO[]) => {
        this.closedAdoptions.set(data);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.errorMessage.set("Error de conexion vuelve a intentarlo mas tarde");
          return;
        }
        if (err.status === 403) {
          this.authService.logout();
          this.router.navigate(["login"]);
        }
        this.errorMessage.set("Ha ocurrido un error cargando los datos");
      }
    });
  }
}
