import { Component, inject, OnInit, signal } from '@angular/core';
import { AdoptionDTO } from '../dto/AdoptionDTO';
import { AdoptionService } from '../adoption-service';
import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { AuthService } from '../../user/auth-service';
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

  closedAdoptions = signal<AdoptionDTO[]>([]);
  errorMessage = signal<string>('');
  loading = signal<boolean>(true);

  ngOnInit(): void {

    this.adoptionService.getHistory().subscribe({
      next: (data: AdoptionDTO[]) => {
        this.closedAdoptions.set(data);
        this.loading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 0) {
          this.loading.set(false);
          this.errorMessage.set("Error de conexion vuelve a intentarlo mas tarde");
          return;
        }
        if (err.status === 403) {
          this.authService.logout("/login");
        }
        this.loading.set(false);
        this.errorMessage.set("Ha ocurrido un error cargando los datos");
      }
    });
  }
}
