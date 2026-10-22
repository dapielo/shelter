import { Component, inject, OnInit, signal } from '@angular/core';
import { AdoptionDTO } from '../../model/AdoptionDTO';
import { AdoptionService } from '../../services/adoption-service';
import { AdoptionGrid } from '../adoption-grid/adoption-grid';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pending-adoptions',
  imports: [AdoptionGrid],
  templateUrl: './pending-adoptions.html',
  styleUrl: './pending-adoptions.css',
})
export class PendingAdoptions implements OnInit{

  auth = inject(AuthService);
  adoptionService = inject(AdoptionService);
  router = inject(Router);

  adoptionsAsOwner = signal<AdoptionDTO[]>([]);
  adoptionsAsAdopter = signal<AdoptionDTO[]>([]);
  errorMessage = signal<string>('');
  message = signal<string>('');

  // cargamos los datos al arrancar el componente
  ngOnInit(): void {
    this.loadData();
  }

  loadData() {

    this.adoptionService.getAdoptionsAsOwner().subscribe({
      next: (data) => {
        this.adoptionsAsOwner.set([...data]);
        this.errorMessage.set('');
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout();
          this.router.navigate(["login"]);
        } else {
          this.errorMessage.set(err.error.message);
        }
      }
    });

    this.adoptionService.getAdoptionsAsAdopter().subscribe({
      next: (data) => {
        this.adoptionsAsAdopter.set([...data]);
        this.errorMessage.set('');
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 403) {
          this.auth.logout();
          this.router.navigate(["login"]);
        }
      }
    });
  }

  // estos métodos que son los que hemos asociado en la plantilla a los emits que nos vienen desde las cards, capturan el valor que hemos emitido 
  onAccept(adoptionId: number) {
    this.adoptionService.acceptAdoption(adoptionId).subscribe({
      next: () => {
        this.message.set("Se ha aceptado la peticion de adopción puedes consultar su email del adoptante en el apartado de historial")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } else {
          this.auth.logout();
          this.message.set('');
          this.errorMessage.set(err.error.message)
          this.router.navigate(["login"]);
        }
      }
    }); 
  }

  onDecline(adoptionId: number) {
    this.adoptionService.declineAdoption(adoptionId).subscribe({
      next: () => {
        this.message.set("Se ha rechazado la peticion de adopción.")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } else {
          this.auth.logout();
          this.message.set('');
          this.errorMessage.set(err.error.message)
          this.router.navigate(["login"]);
        }
      }
    });
    
  }

  onDelete(petId: number) {
    this.adoptionService.deleteAdoption(petId).subscribe({
      next: () => {
        this.message.set("Se ha eliminado la peticion de adopción.")
        this.errorMessage.set('');
        // refrescamos los datos
        this.loadData();
      },
      error: (err:HttpErrorResponse) => {
        if (err.status === 0) {
          this.message.set('');
          this.errorMessage.set('Error de conexion, vuelvelo a intentar más tarde');
        } else {
          this.auth.logout();
          this.message.set('');
          this.errorMessage.set(err.error.message)
          this.router.navigate(["login"]);
        }
      }
    });
    
  }
}
